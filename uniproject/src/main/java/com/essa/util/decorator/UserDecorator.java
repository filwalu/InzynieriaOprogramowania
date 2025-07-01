package com.essa.util.decorator;

import com.essa.model.User;

public abstract class UserDecorator {
    protected User user;
    
    public UserDecorator(User user) {
        this.user = user;
    }
    
    public String getUsername() {
        return user.getUsername();
    }
    
    public String getEmail() {
        return user.getEmail();
    }
    
    public abstract String getDisplayInfo();
}