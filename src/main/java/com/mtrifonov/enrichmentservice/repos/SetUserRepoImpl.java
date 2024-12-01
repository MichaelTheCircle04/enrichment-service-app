package com.mtrifonov.enrichmentservice.repos;

import com.mtrifonov.enrichmentservice.DomainModels.User;
import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 *
 * @Mikhail Trifonov
 */
@Component
public class SetUserRepoImpl implements UserRepository {
    
    private final Set<User> dataSource = new HashSet<>();

    @Override
    public Optional<User> findByMSISDN(String MSISDN) {
        
        try {
            return Optional.of(dataSource.stream().filter(u -> u.getMsisdn().equals(MSISDN)).toList().get(0));
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    @Override
    public synchronized void updateMSISDN(User user, String newMSISDN) { //throw NoSuchElementException if couldn't find user
        User curUser = findByMSISDN(user.getMsisdn()).orElseThrow(() -> new NoSuchElementException("Couldn't find user with given MSISDN"));
        dataSource.remove(curUser);
        curUser.setMsisdn(newMSISDN);
        dataSource.add(curUser);
    }

    @Override
    public Set<User> getUsers() {
        return Collections.unmodifiableSet(dataSource);
    }

    @Override
    public synchronized void addUser(User user) {
        dataSource.add(user);
    }  
}
