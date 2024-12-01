package com.mtrifonov.enrichmentservice;

import com.mtrifonov.enrichmentservice.DomainModels.JSONContent;
import com.mtrifonov.enrichmentservice.DomainModels.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtrifonov.enrichmentservice.DomainModels.User;
import com.mtrifonov.enrichmentservice.messagestorage.MessageStorage;
import com.mtrifonov.enrichmentservice.repos.UserRepository;
import com.mtrifonov.enrichmentservice.validators.MessageValidator;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 *
 * @Mikhail Trifonov
 */
@Component
@Slf4j
public class EnrichmentService {
    
    private final UserRepository repo;
    private final MessageStorage storage;
    private final ObjectMapper mapper;
    private final MessageValidator validator;

    public EnrichmentService(UserRepository repo, MessageStorage storage, ObjectMapper mapper, MessageValidator validator) {
        this.repo = repo;
        this.storage = storage;
        this.mapper = mapper;
        this.validator = validator;
    }
        
    public String enrich(Message message) {
        if (!validator.isValid(message)) {
            storage.updateIncorrect(message);
            return message.getContent();
        }
        
        try {
            JSONContent jc = mapper.readValue(message.getContent(), JSONContent.class);
            User user = repo.findByMSISDN(jc.getMsisdn()).orElseThrow(() -> new NoSuchElementException("Couldn't find user with given MSISDN"));
            jc.setEnrichment(user.getUsername());
            message.setContent(mapper.writeValueAsString(jc));
            storage.updateCorrect(message);  
            return message.getContent();
        } catch (JsonProcessingException | RuntimeException e) {
            log.error(e.getMessage());
            return message.getContent();
        }
    }
}   
