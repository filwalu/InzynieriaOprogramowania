package com.essa.controller;

import com.essa.dto.UserCreateDTO;
import com.essa.dto.UserDTO;
import com.essa.dto.UserUpdateDTO;
import com.essa.mapper.UserMapper;
import com.essa.model.Role;
import com.essa.model.User;
import com.essa.repository.RoleRepository;
import com.essa.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin Users", description = "ADMIN operations on users")
@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public AdminUserController(UserService userService, 
                              UserMapper userMapper,
                              PasswordEncoder passwordEncoder,
                              RoleRepository roleRepository) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.findAll();
        List<UserDTO> userDTOs = users.stream()
                .map(userMapper::toDTO)
                .toList();
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserCreateDTO createDTO) {
        User user = userMapper.toEntity(createDTO);
        
        user.setPassword(passwordEncoder.encode(createDTO.getPassword()));
        
        if (createDTO.getRoleId() != null) {
            Role role = roleRepository.findById(createDTO.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found with id: " + createDTO.getRoleId()));
            user.setRole(role);
        } else {
            Role defaultRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("Default USER role not found"));
            user.setRole(defaultRole);
        }
        
        User savedUser = userService.create(user);
        
        return new ResponseEntity<>(userMapper.toDTO(savedUser), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO updateDTO) {
        User existingUser = userService.findById(id);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }
        
        userMapper.updateEntityFromDto(updateDTO, existingUser);
        
        if (updateDTO.getPassword() != null && !updateDTO.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }
        
        if (updateDTO.getRoleId() != null) {
            Role role = roleRepository.findById(updateDTO.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found with id: " + updateDTO.getRoleId()));
            existingUser.setRole(role);
        }
        
        User updatedUser = userService.update(existingUser);
        
        return ResponseEntity.ok(userMapper.toDTO(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}