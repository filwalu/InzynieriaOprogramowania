package com.essa;

import com.essa.security.CustomUserDetailsService;
import com.essa.security.JwtFilter;
import com.essa.security.JwtUtil;
import com.essa.model.Role;
import com.essa.model.User;
import com.essa.model.Permission;
import com.essa.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SecurityTest {

    private UserRepository userRepository;
    private CustomUserDetailsService userDetailsService;
    private JwtUtil jwtUtil;
    private JwtFilter jwtFilter;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        userDetailsService = new CustomUserDetailsService(userRepository);
        jwtUtil = mock(JwtUtil.class);
        jwtFilter = new JwtFilter(jwtUtil, userDetailsService);
        
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testCustomUserDetailsService_LoadUserByUsername_Found() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ADMIN");
        
        Permission permission = new Permission();
        permission.setId(1L);
        permission.setPermission("READ_USERS");
        permission.setDescription("Can read users");
        permission.setRole(role);
        
        role.setRolePermissions(Set.of(permission));

        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword("encodedPassword");
        user.setRole(role);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

        assertEquals("admin", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("READ_USERS")));
    }

    @Test
    public void testCustomUserDetailsService_LoadUserByUsername_NotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, 
            () -> userDetailsService.loadUserByUsername("nonexistent"));
    }

    @Test
    public void testCustomUserDetailsService_LoadUserByUsername_WithoutPermissions() {
        Role role = new Role();
        role.setId(2L);
        role.setName("USER");
        role.setRolePermissions(Set.of()); // No permissions

        User user = new User();
        user.setId(2L);
        user.setUsername("user");
        user.setPassword("userPassword");
        user.setRole(role);

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("user");

        assertEquals("user", userDetails.getUsername());
        assertEquals("userPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        assertEquals(1, userDetails.getAuthorities().size());
    }

    @Test
    public void testCustomUserDetailsService_LoadUserByUsername_WithoutRole() {
        User user = new User();
        user.setId(3L);
        user.setUsername("norole");
        user.setPassword("password");
        user.setRole(null);
        when(userRepository.findByUsername("norole")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("norole");

        assertEquals("norole", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    
    @Test
    public void testJwtFilter_Integration_NoAuthHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        request.setRequestURI("/api/tickets");
        request.setContextPath("/api");

        jwtFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(filterChain.getRequest() != null);
    }

    @Test
    public void testJwtFilter_Integration_InvalidToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        request.setRequestURI("/api/tickets");
        request.setContextPath("/api");
        request.addHeader("Authorization", "Bearer invalid-jwt-token");

        when(jwtUtil.extractUsername("invalid-jwt-token")).thenThrow(new RuntimeException("Invalid token"));

        jwtFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(filterChain.getRequest() != null);
    }

    @Test
    public void testJwtFilter_Integration_AuthEndpointBypass() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        request.setRequestURI("/api/auth/login");
        request.setContextPath("/api");

        jwtFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(filterChain.getRequest() != null);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    public void testJwtFilter_Integration_SwaggerEndpointBypass() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        request.setRequestURI("/api/swagger-ui/index.html");
        request.setContextPath("/api");

        jwtFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(filterChain.getRequest() != null);
        verifyNoInteractions(jwtUtil);
    }

    
    @Test
    public void testUserDetailsService_MultiplePermissions() {
        Role role = new Role();
        role.setId(1L);
        role.setName("MANAGER");
        
        Permission perm1 = new Permission();
        perm1.setPermission("READ_USERS");
        perm1.setRole(role);
        
        Permission perm2 = new Permission();
        perm2.setPermission("WRITE_USERS");
        perm2.setRole(role);
        
        role.setRolePermissions(Set.of(perm1, perm2));

        User user = new User();
        user.setUsername("manager");
        user.setPassword("password");
        user.setRole(role);

        when(userRepository.findByUsername("manager")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("manager");

        assertEquals(3, userDetails.getAuthorities().size()); // ROLE_MANAGER + 2 permissions
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_MANAGER")));
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("READ_USERS")));
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("WRITE_USERS")));
    }

    @Test
    public void testJwtFilter_Constructor() {
        assertNotNull(jwtFilter);
        JwtFilter newFilter = new JwtFilter(jwtUtil, userDetailsService);
        assertNotNull(newFilter);
    }
}