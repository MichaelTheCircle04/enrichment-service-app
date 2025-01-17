package com.mtrifonov.enrichmentservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtrifonov.enrichmentservice.data.MessageContainer;
import com.mtrifonov.enrichmentservice.data.UserContainer;
import com.mtrifonov.enrichmentservice.domainmodels.JSONContent;
import com.mtrifonov.enrichmentservice.domainmodels.Message;
import com.mtrifonov.enrichmentservice.messagestorage.MessageStorage;
import com.mtrifonov.enrichmentservice.repos.UserRepository;
import com.mtrifonov.enrichmentservice.validators.MessageValidator;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
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
    public void enrichmentServiceTest() throws InterruptedException, ExecutionException {
        
        var latch = new CountDownLatch(1);
		var futures = container.getValidMessages().stream().map(m -> CompletableFuture.supplyAsync(() -> {
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return es.enrich(new Message(m.getContent(), m.getEnrichment()));
		}, service).thenApply(s -> !s.equals(m.getContent()))).toList();
		
		var future = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
		latch.countDown();
		future.join();
		
        for (var f : futures) {
        	var res = (CompletableFuture<Boolean>) f;
        	assertTrue(res.get());
        }
    }
    
    @Test 
    @DirtiesContext
    public void enrichmentServiceTestWithUserInfoChanges() throws JsonProcessingException {
        
        var messages = container.getValidMessages().stream().filter(m -> {
            JSONContent content;
            try {
                content = mapper.readValue(m.getContent(), JSONContent.class);
            } catch (JsonProcessingException e) {
                return false;
            }
            return (content.getMsisdn().equals("+78005553535") || content.getMsisdn().equals("88005553565"));
        }).toList();
        
        var latch = new CountDownLatch(1);
        userContainer.getUsersForUpdate().stream().map(u -> {
        	try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	return CompletableFuture.runAsync(() -> repo.updateUsername(u, u.getUsername()), service);
        }).close();
                
        var futures = messages.stream().map(m -> CompletableFuture.supplyAsync(() -> {
        	try {
        		latch.await();
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	return es.enrich(m);
        	}, service)).toList();
        
        var ready = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        latch.countDown();
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
