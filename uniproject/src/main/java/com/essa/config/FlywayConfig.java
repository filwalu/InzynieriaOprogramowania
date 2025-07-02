package com.essa.config;

import com.essa.model.Role;
import com.essa.model.User;
import com.essa.repository.RoleRepository;
import com.essa.repository.UserRepository;
import org.flywaydb.core.Flyway;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Optional;

@Configuration
public class FlywayConfig {

    private final DataSource dataSource;

    public FlywayConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public Flyway flyway() {
        Flyway flyway = Flyway.configure().dataSource(dataSource).load();
        flyway.migrate();
        return flyway;
    }

    @Component
    public static class AdminInitializer implements ApplicationListener<ContextRefreshedEvent> {

        private boolean alreadyInitialized = false;
        private final UserRepository userRepository;
        private final RoleRepository roleRepository;
        private final PasswordEncoder passwordEncoder;

        public AdminInitializer(UserRepository userRepository, RoleRepository roleRepository, 
                          PasswordEncoder passwordEncoder) {
            this.userRepository = userRepository;
            this.roleRepository = roleRepository;
            this.passwordEncoder = passwordEncoder;
        }

        @Override
        @Transactional
        public void onApplicationEvent(@org.springframework.lang.NonNull ContextRefreshedEvent event) {
            if (alreadyInitialized) {
                return;
            }
            
            if (userRepository.findByUsername("admin").isEmpty()) {
                Optional<Role> adminRole = roleRepository.findByName("ADMIN");
                if (adminRole.isPresent()) {
                    User adminUser = new User();
                    adminUser.setUsername("admin");
                    adminUser.setPassword(passwordEncoder.encode("admin123")); // Domyślne hasło
                    adminUser.setFirstname("Admin");
                    adminUser.setLastname("User");
                    adminUser.setEmail("admin@example.com");
                    adminUser.setRole(adminRole.get());
                    adminUser.setCreatedAt(LocalDateTime.now());
                    adminUser.setUpdatedAt(LocalDateTime.now());
                    
                    userRepository.save(adminUser);
                }
            }
            
            alreadyInitialized = true;
        }
    }
}