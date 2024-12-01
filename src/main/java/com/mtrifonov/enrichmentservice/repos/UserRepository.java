package com.mtrifonov.enrichmentservice.repos;

import com.mtrifonov.enrichmentservice.DomainModels.User;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @Mikhail Trifonov
 */
public interface UserRepository {
    
    public Optional<User> findByMSISDN(String MSISDN);
    public void updateMSISDN(User user, String newMSISDN);
    public Set<User> getUsers();
    public void addUser(User user);
}
