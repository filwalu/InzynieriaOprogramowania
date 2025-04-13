package com.essa.repository;

import com.essa.model.User;
import com.essa.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByLastname(String lastname);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}