package com.essa;

import com.essa.model.Role;
import com.essa.model.TicketPriority;
import com.essa.model.TicketStatus;
import com.essa.model.User;
import com.essa.repository.RoleRepository;
import com.essa.repository.UserRepository;
import com.essa.security.JwtUtil;
import com.essa.service.TicketService;
import com.essa.service.UserService;
import com.essa.service.impl.UserServiceImpl;
import com.essa.util.observer.UserObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class AppTest {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private UserService userService;
    private TicketService ticketService;
    private UserObserver userObserver;

    @BeforeEach
    public void setup() {
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        ticketService = mock(TicketService.class);
        userObserver = mock(UserObserver.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    public void testFindById_UserExists() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User found = userService.findById(1L);
        assertEquals(1L, found.getId());
    }

    @Test
    public void testFindById_UserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> userService.findById(2L));
        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    public void testCreateUser_Success() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        User created = userService.create(user);
        assertEquals("testuser", created.getUsername());
    }

    @Test
    public void testCreateUser_UsernameExists() {
        User user = new User();
        user.setUsername("existing");
        when(userRepository.existsByUsername("existing")).thenReturn(true);
        Exception exception = assertThrows(RuntimeException.class, () -> userService.create(user));
        assertTrue(exception.getMessage().contains("User already exists"));
    }

    @Test
    public void testUpdateUser_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("updateuser");
        user.setEmail("update@example.com");
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findByUsername("updateuser")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("update@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        User updated = userService.update(user);
        assertEquals("updateuser", updated.getUsername());
    }

    @Test
    public void testDeleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);
        assertDoesNotThrow(() -> userService.delete(1L));
    }

    @Test
    public void testDeleteUser_NotFound() {
        when(userRepository.existsById(2L)).thenReturn(false);
        Exception exception = assertThrows(RuntimeException.class, () -> userService.delete(2L));
        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    public void testDeleteUser_NotFoundDifId() {
        when(userRepository.existsById(777L)).thenReturn(false);
        Exception exception = assertThrows(RuntimeException.class, () -> userService.delete(777L));
        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    public void testHasPermission_True() {
        Role role = new Role();
        role.setRolePermissions(Set.of(new com.essa.model.Permission(1L, "PERM", "desc", role)));
        User user = new User();
        user.setId(1L);
        user.setRole(role);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertTrue(userService.hasPermission(1L, "PERM"));
    }

    @Test
    public void testHasPermission_False() {
        Role role = new Role();
        role.setRolePermissions(Set.of(new com.essa.model.Permission(1L, "PERMM123", "desc", role)));
        User user = new User();
        user.setId(1L);
        user.setRole(role);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertFalse(userService.hasPermission(1L, "PERM"));
    }

    @Test
    public void testHasWrongPermission_Trues() {
        Role role = new Role();
        role.setRolePermissions(Set.of());
        User user = new User();
        user.setId(1L);
        user.setRole(role);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertFalse(userService.hasPermission(1L, "PERM"));
    }
    
    @Test
    public void testJwtUtil_InvalidToken() {
        JwtUtil jwtUtil = new JwtUtil();
        try {
            var secretField = JwtUtil.class.getDeclaredField("secret");
            secretField.setAccessible(true);
            secretField.set(jwtUtil, "ThisIsAVerySecureSecretKeyThatShouldBeChangedInProduction");
            var expField = JwtUtil.class.getDeclaredField("expiration");
            expField.setAccessible(true);
            expField.set(jwtUtil, 100000L);
        } catch (Exception e) {
            fail("Reflection failed");
        }
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("admin")
                .password("admin123")
                .authorities("ROLE_ADMIN")
                .build();
        String fakeToken = "invalid.token.value";
        assertThrows(io.jsonwebtoken.JwtException.class, () -> jwtUtil.validateToken(fakeToken, userDetails));
    }

    @Test
    public void testJwtUtil_GenerateAndValidateToken() {
        JwtUtil jwtUtil = new JwtUtil();
        // Set secret and expiration via reflection for test
        try {
            var secretField = JwtUtil.class.getDeclaredField("secret");
            secretField.setAccessible(true);
            secretField.set(jwtUtil, "ThisIsAVerySecureSecretKeyThatShouldBeChangedInProduction");
            var expField = JwtUtil.class.getDeclaredField("expiration");
            expField.setAccessible(true);
            expField.set(jwtUtil, 100000L);
        } catch (Exception e) {
            fail("Reflection failed");
        }
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("admin")
                .password("admin123")
                .authorities("ROLE_ADMIN")
                .build();
        String token = jwtUtil.generateToken(userDetails);
        assertNotNull(token);
        assertEquals("admin", jwtUtil.extractUsername(token));
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    public void testPasswordEncoder() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String raw = "password";
        String encoded = encoder.encode(raw);
        assertTrue(encoder.matches(raw, encoded));
    }

    @Test
    public void testCreateUser_EmailExists() {
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("existing@example.com");
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);
        Exception exception = assertThrows(RuntimeException.class, () -> userService.create(user));
        assertTrue(exception.getMessage().contains("User already exists"));
    }

    @Test
    public void testUpdateUser_NotFound() {
        User user = new User();
        user.setId(99L);
        when(userRepository.existsById(99L)).thenReturn(false);
        Exception exception = assertThrows(RuntimeException.class, () -> userService.update(user));
        assertTrue(exception.getMessage().contains("User not found"));
    }
    
    @Test
    public void testTicketStatus_True() {
        assertEquals(TicketStatus.OPEN, TicketStatus.valueOf("OPEN"));
        assertEquals(TicketStatus.RESOLVED, TicketStatus.valueOf("RESOLVED"));
    }

    @Test
    public void testTicketStatus_Wrong() {
        assertNotEquals(TicketStatus.CLOSED, TicketStatus.OPEN);
        assertNotEquals(TicketStatus.IN_PROGRESS, TicketStatus.WAITING_FOR_CUSTOMER);
    }

    @Test
    public void testTicketStatus_False() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> TicketStatus.valueOf("NOT_A_STATUS"));
        assertTrue(exception.getMessage().contains("No enum constant"));
    }

    @Test
    public void testTicketPriority_True() {
        assertEquals(TicketPriority.HIGH, TicketPriority.valueOf("HIGH"));
        assertEquals(TicketPriority.CRITICAL, TicketPriority.valueOf("CRITICAL"));
    }

    @Test
    public void testTicketPriority_Wrong() {
        assertNotEquals(TicketPriority.LOW, TicketPriority.HIGH);
        assertNotEquals(TicketPriority.MEDIUM, TicketPriority.CRITICAL);
    }

    @Test
    public void testTicketPriority_False() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> TicketPriority.valueOf("NOT_A_PRIORITY"));
        assertTrue(exception.getMessage().contains("No enum constant"));
    }
    
    @Test
    public void testFindRoleByName_CallsRepository() {
        RoleRepository mockRoleRepo = mock(RoleRepository.class);
        when(mockRoleRepo.findByName("ADMIN")).thenReturn(Optional.of(new Role()));
        Optional<Role> result = mockRoleRepo.findByName("ADMIN");
        assertTrue(result.isPresent());
        verify(mockRoleRepo, times(1)).findByName("ADMIN");
    }
}