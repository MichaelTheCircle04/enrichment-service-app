package com.mtrifonov.enrichmentservice.messagestorage;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.stereotype.Component;

import com.mtrifonov.enrichmentservice.domainmodels.Message;

/**
 *
 * @Mikhal Trifonov
 */
@Component
@Data
public class MessageStorage {
    private final List<Message> correct = new ArrayList<>();
    private final List<Message> incorrect = new ArrayList<>();

    public void updateCorrect(Message message) {
        correct.add(message);
    }
    
    public void updateIncorrect(Message message) {
        incorrect.add(message);
    }
}
