package com.mtrifonov.enrichmentservice.setups;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtrifonov.enrichmentservice.DomainModels.Message;
import com.mtrifonov.enrichmentservice.DomainModels.Message.EnrichmentType;
import com.mtrifonov.enrichmentservice.DomainModels.User;
import com.mtrifonov.enrichmentservice.repos.MessageContainer;
import com.mtrifonov.enrichmentservice.repos.UserContainer;
import com.mtrifonov.enrichmentservice.repos.UserRepository;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 *
 * @Mikhail Trifonov
 */
@Configuration
@Slf4j
@Profile("test")
public class SetupConfig {
    
    @Value("${users.base}")
    private String usersLocation;
    @Value("${users.for-update}")
    private String usersForUpdateLocation;
    @Value("${messages.valid}")
    private String messagesLocation;
    @Value("${messages.invalid}")
    private String invalidMessagesLocation;
        
    @Bean
    public CommandLineRunner setup(UserRepository repo, MessageContainer container, UserContainer userContainer, ObjectMapper mapper) {
        return args -> {
            File usersSrc = new File(usersLocation);
            File usersForUpdateSrc = new File(usersForUpdateLocation);
            File messagesSrc = new File(messagesLocation);
            File invalidMessagesSrc = new File(invalidMessagesLocation);
            
            Set<User> users = mapper.readValue(usersSrc, new TypeReference<Set<User>>(){});
            users.forEach(u -> repo.addUser(u));
            
            BufferedReader reader = new BufferedReader(new FileReader(messagesSrc));
            String rawJson = reader.lines().map(l -> l.trim()).collect(Collectors.joining());
            List<String> contents = convertToContent(rawJson.substring(1, rawJson.length() - 1));
            List<Message> messages = contents.stream().map(c -> new Message(c, EnrichmentType.MSISDN)).toList();
            messages.forEach(m -> container.addValidMessage(m));
            
            reader = new BufferedReader(new FileReader(invalidMessagesSrc));
            rawJson = reader.lines().map(l -> l.trim()).collect(Collectors.joining());
            contents = convertToContent(rawJson.substring(1, rawJson.length() - 1));
            messages = contents.stream().map(c -> new Message(c, EnrichmentType.MSISDN)).toList();
            messages.forEach(m -> container.addInvalidMessage(m));
            
            users = mapper.readValue(usersForUpdateSrc, new TypeReference<Set<User>>(){});
            users.forEach(u -> userContainer.addUserForUpdate(u));
        };
    }
    private List<String> convertToContent(String json) {
                
        if (json.equals("")) {
            return Collections.EMPTY_LIST;
        }
        
        List<String> content = new ArrayList<>();
        int cnt = 0;
        int first = 0;
        
        for (int i = 0; i < json.length(); i++) {
            if (json.charAt(i) == ',' && cnt == 0) {
                content.add(json.substring(first, i));
                first = ++i;
            }
            if (json.charAt(i) == '{') {
                cnt++;
            } else if (json.charAt(i) == '}') {
                cnt--;
            }
        }
        
        content.add(json.substring(first));
        return content;   
    }
}
