package com.mtrifonov.enrichmentservice.repos;

import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Component;

import com.mtrifonov.enrichmentservice.domainmodels.User;
import com.mtrifonov.enrichmentservice.domainmodels.Username;

/**
 *
 * @Mikhail Trifonov
 */
@Component
public class SetUserRepoImpl implements UserRepository {
    
    private final Set<User> dataSource = new HashSet<>();
    private final ReentrantLock lock = new ReentrantLock(true);
    private final AtomicInteger seq = new AtomicInteger(1);

    @Override
    public Optional<User> findByMSISDN(String MSISDN) {  
    	lock.lock();
    	
        try {
            return Optional.of(dataSource.stream().filter(u -> u.getMsisdn().equals(MSISDN)).toList().get(0));
        } catch (RuntimeException e) {
            return Optional.empty();
        } finally {
        	lock.unlock();
        }
    }

    @Override
    public void updateMSISDN(User user, String newMSISDN) { //throw NoSuchElementException if couldn't find user  	
    	lock.lock();
    	
        try {
        	var curUser = findByMSISDN(user.getMsisdn()).orElseThrow(() -> new NoSuchElementException("Couldn't find user with given MSISDN"));
        	dataSource.remove(curUser);
            curUser.setMsisdn(newMSISDN);
            dataSource.add(curUser);
        } catch (NoSuchElementException e) {
        	throw e;
        } finally {
        	lock.unlock();
        }
        
    }
    
    @Override
    public void updateUsername(User user, Username newUsername) { //throw NoSuchElementException if couldn't find user   	
    	lock.lock();
    	
    	try {
    		var curUser = findByMSISDN(user.getMsisdn()).orElseThrow(() -> new NoSuchElementException("Couldn't find user with given MSISDN"));
    		dataSource.remove(curUser);
    		curUser.setUsername(newUsername);
    		dataSource.add(curUser);
    	} catch (NoSuchElementException e) {
        	throw e;
    	} finally {
    		lock.unlock();
    	}
    }

    @Override
    public Set<User> getUsers() {
        return Collections.unmodifiableSet(dataSource);
    }

    @Override
    public void addUser(User user) {
    	lock.lock();
    	
    	try {
    		user.setId(seq.getAndIncrement());
    		dataSource.add(user);
    	} finally {
    		lock.unlock();
    	}
    }  
}
