package com.mtrifonov.enrichmentservice.repos;

import com.mtrifonov.enrichmentservice.DomainModels.User;
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
public class UserContainer {
    
    private final Set<User> usersForUpdate = new HashSet<>();
    
    public void addUserForUpdate(User user) {
        usersForUpdate.add(user);
    }
    
    public Set<User> getUsersForUpdate() {
        return Collections.unmodifiableSet(usersForUpdate);
    }
    
}
