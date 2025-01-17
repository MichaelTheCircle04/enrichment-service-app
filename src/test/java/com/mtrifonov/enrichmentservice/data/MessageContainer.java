package com.mtrifonov.enrichmentservice.data;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.mtrifonov.enrichmentservice.domainmodels.Message;

/**
 *
 * @Mikhail Trifonov
 */
@Component
@Profile("test")
public class MessageContainer {
    
    private final Set<Message> validMessages = new HashSet<>();
    private final Set<Message> invalidMessages = new HashSet<>();
    
    public void addValidMessage(Message message) {
        validMessages.add(message);
    }
    
    public Set<Message> getValidMessages() {
        return Collections.unmodifiableSet(validMessages);
    }
    
    public void addInvalidMessage(Message message) {
        invalidMessages.add(message);
    }
    
    public Set<Message> getInvalidMessages() {
        return Collections.unmodifiableSet(invalidMessages);
    }
}
