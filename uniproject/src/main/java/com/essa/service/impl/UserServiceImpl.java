package com.essa.service.impl;

import com.essa.model.User;
import com.essa.repository.UserRepository;
import com.essa.service.UserService;
import com.essa.util.decorator.UserWithStatsDecorator;
import com.essa.util.observer.UserLoggingObserver;
import com.essa.util.observer.UserSubject;
import com.essa.util.command.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.Logger;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserSubject userSubject;
    private final UserCommandInvoker commandInvoker = new UserCommandInvoker();

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;

        this.userSubject = new UserSubject();
        this.userSubject.addObserver(new UserLoggingObserver()); // Observer for logging
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User create(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("User already exists with username: " + user.getUsername());
        }
        
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User already exists with email: " + user.getEmail());
        }
        
        User savedUser = userRepository.save(user);

        
        if (savedUser.getId() != null) {
            userSubject.notifyUserCreated(savedUser.getId(), savedUser.getUsername());
            deactivateUserWithCommand(savedUser.getId()); // Deactivate for showing purpose
            activateUserWithCommand(savedUser.getId()); // Activate user after creation for showing purpose
        }
       
        return savedUser;
    }

    @Override
    @Transactional
    public User update(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new RuntimeException("User not found with id: " + user.getId());
        }
        
        userRepository.findByUsername(user.getUsername())
                .ifPresent(existingUser -> {
                    if (!existingUser.getId().equals(user.getId())) {
                        throw new RuntimeException("Username already taken: " + user.getUsername());
                    }
                });
        
        if (user.getEmail() != null) {
            userRepository.findByEmail(user.getEmail())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(user.getId())) {
                            throw new RuntimeException("Email already taken: " + user.getEmail());
                        }
                    });
        }
        
        User updatedUser = userRepository.save(user);
        
        userSubject.notifyUserUpdated(updatedUser.getId(), updatedUser.getUsername());
    
        return updatedUser;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        
        userRepository.deleteById(id);
    }

    @Override
    public boolean hasPermission(Long userId, String permissionName) {
        User user = findById(userId);
        
        return user.getRole().getRolePermissions().stream()
                .anyMatch(rp -> rp.getPermission().equals(permissionName));
    }
    
    public String getUserWithStats(Long userId) {
        User user = findById(userId);
        //Mocking due circular dependency with TicketService
        int createdTickets = Math.abs(user.getUsername().hashCode() % 10); // Deterministic mock value
        int assignedTickets = Math.abs(user.getEmail().hashCode() % 5); // Deterministic mock value
        
        UserWithStatsDecorator decorator = new UserWithStatsDecorator(user, createdTickets, assignedTickets);
        String info = decorator.getDisplayInfo();
        
        Logger.getLogger(UserServiceImpl.class.getName()).info("DECORATOR: " + info);
        return info;
    }

    public void activateUserWithCommand(Long userId) {
        User user = findById(userId);
        UserCommand command = new ActivateUserCommand(user);
        commandInvoker.executeCommand(command);
    }

    public void deactivateUserWithCommand(Long userId) {
        User user = findById(userId);
        UserCommand command = new DeactivateUserCommand(user);
        commandInvoker.executeCommand(command);
    }
}
