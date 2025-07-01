package com.essa.util.observer;

import java.util.logging.Logger;

public class UserLoggingObserver implements UserObserver {
    private static final Logger logger = Logger.getLogger(UserLoggingObserver.class.getName());
    
    @Override
    public void onUserCreated(Long userId, String username) {
        logger.info("OBSERVER: User created - ID: " + userId + ", Username: " + username);
    }
    
    @Override
    public void onUserUpdated(Long userId, String username) {
        logger.info("OBSERVER: User updated - ID: " + userId + ", Username: " + username);
    }
}