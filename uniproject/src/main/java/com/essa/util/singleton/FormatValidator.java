package com.essa.util.singleton;

import java.util.regex.Pattern;
//singleton class for validating email and username formats
public class FormatValidator {
    private static FormatValidator instance;
    private final Pattern emailPattern;
    private final Pattern usernamePattern;
    
    private FormatValidator() {
        emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        usernamePattern = Pattern.compile("^[A-Za-z0-9_]{3,20}$");
    }
    
    public static FormatValidator getInstance() {
        if (instance == null) {
            synchronized (FormatValidator.class) {
                if (instance == null) {
                    instance = new FormatValidator();
                }
            }
        }
        return instance;
    }
    
    public boolean isValidEmail(String email) {
        boolean isValid = email != null && emailPattern.matcher(email).matches();
        System.out.println("Email validation for: " + email + " - Result: " + isValid);
        return isValid;
    }
    
    public boolean isValidUsername(String username) {
        boolean isValid = username != null && usernamePattern.matcher(username).matches();
        System.out.println("Username validation for: " + username + " - Result: " + isValid);
        return isValid;
    }
} 