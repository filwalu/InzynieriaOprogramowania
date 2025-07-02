package com.essa.util.observer;

public interface UserObserver {
    void onUserCreated(Long userId, String username);
    void onUserUpdated(Long userId, String username);
}