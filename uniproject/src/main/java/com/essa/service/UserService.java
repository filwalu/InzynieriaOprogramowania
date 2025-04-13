package com.essa.service;

import com.essa.model.User;
import java.util.List;

public interface UserService {
    User findById(Long id);
    User findByUsername(String username);
    List<User> findAll();
    User create(User user);
    User update(User user);
    void delete(Long id);
    boolean hasPermission(Long userId, String permissionName);
}