package com.mtrifonov.enrichmentservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtrifonov.enrichmentservice.DomainModels.JSONContent;
import com.mtrifonov.enrichmentservice.DomainModels.Message;
import com.mtrifonov.enrichmentservice.DomainModels.User;
import com.mtrifonov.enrichmentservice.messagestorage.MessageStorage;
import com.mtrifonov.enrichmentservice.repos.MessageContainer;
import com.mtrifonov.enrichmentservice.repos.UserContainer;
import com.mtrifonov.enrichmentservice.repos.UserRepository;
import com.mtrifonov.enrichmentservice.validators.MessageValidator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

/**
 *
 * @Mikhail Trifonov
 */
@SpringBootTest
public class EnrichmentServiceApplicationTest {
    
    private final EnrichmentService es;
    private final MessageValidator validator;
    private final MessageContainer container;
    private final UserContainer userContainer;
    private final UserRepository repo;
    private final MessageStorage storage;
    private final ObjectMapper mapper;
    private final ExecutorService service = Executors.newFixedThreadPool(8);
    
    @Autowired
    public EnrichmentServiceApplicationTest(EnrichmentService es,
            MessageValidator validator,
            MessageContainer container,
            UserContainer userContainer,
            UserRepository repo,
            MessageStorage storage,
            ObjectMapper mapper) {
        this.es = es;
        this.validator = validator;
        this.container = container;
        this.userContainer = userContainer;
        this.storage = storage;
        this.mapper = mapper;
        this.repo = repo;
    }
    
    @Test 
    public void messageValidatorTest() throws IOException {
                
        Message validMessage = container.getValidMessages().iterator().next();
        
        assertTrue(validator.isValid(validMessage));
        
        for (Message m : container.getInvalidMessages()) {
            assertFalse(validator.isValid(m));
        }
    }
    
    @Test
    @DirtiesContext
    public void enrichmentServiceTest() {
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        for (Message m : container.getValidMessages()) {
            futures.add(CompletableFuture.supplyAsync(() -> es.enrich(new Message(m.getContent(), m.getEnrichment())), service).thenApply(s -> !s.equals(m.getContent())));
        }
        
        futures.forEach(f -> {
            assertTrue(f.join());
        }); 
    }
    
    @Test 
    @DirtiesContext
    public void enrichmentServiceTestWithUserInfoChanges() throws JsonProcessingException {
        
        List<Message> messages = container.getValidMessages().stream().filter(m -> {
            JSONContent content;
            try {
                content = mapper.readValue(m.getContent(), JSONContent.class);
            } catch (JsonProcessingException e) {
                return false;
            }
            return (content.getMsisdn().equals("+78005553535") || content.getMsisdn().equals("88005553565"));
        }).toList();
        
        for (User u : userContainer.getUsersForUpdate()) {
           service.submit(() -> repo.updateUsername(u, u.getUsername()));
        }
        
        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (Message m : messages) {
            futures.add(CompletableFuture.supplyAsync(() -> es.enrich(m), service));
        }
        
        CompletableFuture<Void> ready = CompletableFuture.allOf(futures.get(0), futures.get(1));
        ready.join();
        
        for (Message m : storage.getCorrect()) {
            JSONContent content;
            try {
                content = mapper.readValue(m.getContent(), JSONContent.class);
            } catch (JsonProcessingException e) {
                content = new JSONContent();
            }
            assertTrue(content.getEnrichment().equals(repo.findByMSISDN(content.getMsisdn()).get().getUsername()));
        }
        
        
        
    }
}
