package com.essa.util.observer;

import java.util.ArrayList;
import java.util.List;

public class UserSubject {
    private List<UserObserver> observers = new ArrayList<>();
    
    public void addObserver(UserObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(UserObserver observer) {
        observers.remove(observer);
    }
    
    public void notifyUserCreated(Long userId, String username) {
        for (UserObserver observer : observers) {
            observer.onUserCreated(userId, username);
        }
    }
    
    public void notifyUserUpdated(Long userId, String username) {
        for (UserObserver observer : observers) {
            observer.onUserUpdated(userId, username);
        }
    }
}