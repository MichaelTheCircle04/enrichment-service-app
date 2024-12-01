package com.mtrifonov.enrichmentservice.messagestorage;

import com.mtrifonov.enrichmentservice.DomainModels.Message;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.stereotype.Component;

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
