package com.mtrifonov.enrichmentservice;

import com.mtrifonov.enrichmentservice.DomainModels.Message;
import com.mtrifonov.enrichmentservice.repos.MessageContainer;
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
                
        Message validMessage = container.getValidMessages().iterator().next();
        
        assertTrue(validator.isValid(validMessage));
        
        for (Message m : container.getInvalidMessages()) {
            assertFalse(validator.isValid(m));
        }
    }
    
    @Test
    public void enrichmentServiceTest() {
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        for (Message m : container.getValidMessages()) {
            futures.add(CompletableFuture.supplyAsync(() -> es.enrich(new Message(m.getContent(), m.getEnrichment())), service).thenApply(s -> !s.equals(m.getContent())));
        }
        
        futures.forEach(f -> {
            assertTrue(f.join());
        });
        
        
    }
}
