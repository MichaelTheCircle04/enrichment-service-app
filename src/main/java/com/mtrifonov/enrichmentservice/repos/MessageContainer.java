package com.mtrifonov.enrichmentservice.repos;

import com.mtrifonov.enrichmentservice.DomainModels.Message;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 *
 * @Mikhail Trifonov
 */
@Component
@Profile("test")
public class MessageContainer {
    
    private final Set<Message> messages = new HashSet<>();
    
    public void addMessage(Message message) {
        messages.add(message);
    }
    
    public Set<Message> getMessages() {
        return Collections.unmodifiableSet(messages);
    }
}
