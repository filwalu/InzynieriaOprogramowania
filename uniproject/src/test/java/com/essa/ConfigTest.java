package com.essa;

import com.essa.config.OpenApiConfig;
import com.essa.config.SecurityConfig;
import com.essa.security.JwtFilter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConfigTest {

    private JwtFilter jwtFilter;
    private SecurityConfig securityConfig;
    private OpenApiConfig openApiConfig;

    @BeforeEach
    public void setUp() {
        jwtFilter = mock(JwtFilter.class);
        securityConfig = new SecurityConfig(jwtFilter);
        openApiConfig = new OpenApiConfig();
    }

    @Test
    public void testSecurityConfig_PasswordEncoder() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
        
        String encoded = passwordEncoder.encode("password");
        assertTrue(passwordEncoder.matches("password", encoded));
        assertFalse(passwordEncoder.matches("wrongpassword", encoded));
    }

    @Test
    public void testSecurityConfig_AuthenticationManager() throws Exception {
        AuthenticationConfiguration authConfig = mock(AuthenticationConfiguration.class);
        when(authConfig.getAuthenticationManager()).thenReturn(mock(org.springframework.security.authentication.AuthenticationManager.class));
        
        var authManager = securityConfig.authenticationManager(authConfig);
        
        assertNotNull(authManager);
        verify(authConfig).getAuthenticationManager();
    }

    @Test
    public void testOpenApiConfig_CustomOpenAPI() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        
        assertNotNull(openAPI);
        assertEquals("Ticketing API", openAPI.getInfo().getTitle());
        assertEquals("1.0", openAPI.getInfo().getVersion());
        
        assertNotNull(openAPI.getComponents());
        assertNotNull(openAPI.getComponents().getSecuritySchemes());
        assertTrue(openAPI.getComponents().getSecuritySchemes().containsKey("bearerAuth"));
        
        SecurityScheme bearerAuth = openAPI.getComponents().getSecuritySchemes().get("bearerAuth");
        assertEquals(SecurityScheme.Type.HTTP, bearerAuth.getType());
        assertEquals("bearer", bearerAuth.getScheme());
        assertEquals("JWT", bearerAuth.getBearerFormat());
    }

    @Test
    public void testOpenApiConfig_SecurityRequirement() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        
        assertNotNull(openAPI.getSecurity());
        assertFalse(openAPI.getSecurity().isEmpty());
        assertTrue(openAPI.getSecurity().get(0).containsKey("bearerAuth"));
    }

    @Test
    public void testPasswordEncoder_DifferentPasswords() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String password1 = "password1";
        String password2 = "password2";
        
        String encoded1 = encoder.encode(password1);
        String encoded2 = encoder.encode(password2);
        
        assertNotEquals(encoded1, encoded2);
        assertTrue(encoder.matches(password1, encoded1));
        assertTrue(encoder.matches(password2, encoded2));
        assertFalse(encoder.matches(password1, encoded2));
        assertFalse(encoder.matches(password2, encoded1));
    }

    @Test
    public void testPasswordEncoder_SamePasswordDifferentEncoding() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String password = "samepassword";
        String encoded1 = encoder.encode(password);
        String encoded2 = encoder.encode(password);
        
        // BCrypt generates different hashes for the same password (salt)
        assertNotEquals(encoded1, encoded2);
        
        // But both should match the original password
        assertTrue(encoder.matches(password, encoded1));
        assertTrue(encoder.matches(password, encoded2));
    }
}