package com.essa.service.impl;

import com.essa.model.User;
import com.essa.repository.UserRepository;
import com.essa.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        
        return userRepository.save(user);
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
        
        return userRepository.save(user);
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
}
