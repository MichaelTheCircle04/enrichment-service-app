package com.mtrifonov.enrichmentservice.repos;

import java.util.Optional;
import java.util.Set;

import com.mtrifonov.enrichmentservice.domainmodels.User;
import com.mtrifonov.enrichmentservice.domainmodels.Username;

/**
 *
 * @Mikhail Trifonov
 */
public interface UserRepository {
    
    public Optional<User> findByMSISDN(String MSISDN);
    public void updateMSISDN(User user, String newMSISDN);
    public void updateUsername(User user, Username newUsername);
    public Set<User> getUsers();
    public void addUser(User user);
}
