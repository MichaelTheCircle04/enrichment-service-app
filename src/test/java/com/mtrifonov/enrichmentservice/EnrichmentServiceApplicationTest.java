package com.mtrifonov.enrichmentservice;

import com.mtrifonov.enrichmentservice.DomainModels.Message;
import com.mtrifonov.enrichmentservice.DomainModels.Message.EnrichmentType;
import com.mtrifonov.enrichmentservice.repos.MessageContainer;
import com.mtrifonov.enrichmentservice.validators.MessageValidator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @Mikhail Trifonov
 */
@SpringBootTest
public class EnrichmentServiceApplicationTest {
    
    private final EnrichmentService es;
    private final MessageValidator validator;
    private final MessageContainer container;
    private final ExecutorService service = Executors.newFixedThreadPool(8);
    
    @Autowired
    public EnrichmentServiceApplicationTest(EnrichmentService es, MessageValidator validator, MessageContainer container) {
        this.es = es;
        this.validator = validator;
        this.container = container;
    }
    
    @Test 
    public void messageValidatorTest() throws IOException {
                
        Message validMessage = new Message();
        Message invalidMessage = new Message();
        
        File valid = new File("src/main/resources/valid-message.json");
        BufferedReader validReader = new BufferedReader(new FileReader(valid));
        File invalid = new File("src/main/resources/invalid-message.json");
        BufferedReader invalidReader = new BufferedReader(new FileReader(invalid));
        
        String validContent = validReader.lines().map(l -> l.strip()).collect(Collectors.joining());
        String invalidContent = invalidReader.lines().map(l -> l.strip()).collect(Collectors.joining());
        
        validMessage.setContent(validContent);
        validMessage.setEnrichment(EnrichmentType.MSISDN);
        invalidMessage.setContent(invalidContent);
        invalidMessage.setEnrichment(EnrichmentType.MSISDN);
        
        assertTrue(validator.isValid(validMessage));
        assertFalse(validator.isValid(invalidMessage));
    }
    
    @Test
    public void enrichmentServiceTest() {
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        for (Message m : container.getMessages()) {
            futures.add(CompletableFuture.supplyAsync(() -> es.enrich(new Message(m.getContent(), m.getEnrichment())), service).thenApply(s -> !s.equals(m.getContent())));
        }
        
        futures.forEach(f -> {
            assertTrue(f.join());
        });
        
        
    }
}
