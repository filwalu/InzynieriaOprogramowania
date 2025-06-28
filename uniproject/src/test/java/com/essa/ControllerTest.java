package com.essa;

import com.essa.controller.AdminUserController;
import com.essa.controller.AuthController;
import com.essa.controller.TicketController;
import com.essa.dto.*;
import com.essa.mapper.TicketMapper;
import com.essa.model.*;
import com.essa.security.JwtUtil;
import com.essa.service.TicketService;
import com.essa.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ControllerTest {

    private TicketService ticketService;
    private UserService userService;
    private TicketMapper ticketMapper;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private UserDetailsService userDetailsService;
    private JwtUtil jwtUtil;

    private TicketController ticketController;
    private AdminUserController adminUserController;
    private AuthController authController;

    @BeforeEach
    public void setUp() {
        ticketService = mock(TicketService.class);
        userService = mock(UserService.class);
        ticketMapper = mock(TicketMapper.class);
        passwordEncoder = mock(PasswordEncoder.class);
        authenticationManager = mock(AuthenticationManager.class);
        userDetailsService = mock(UserDetailsService.class);
        jwtUtil = mock(JwtUtil.class);

        ticketController = new TicketController(ticketService, userService, ticketMapper);
        adminUserController = new AdminUserController(userService, passwordEncoder);
        authController = new AuthController(authenticationManager, userDetailsService, jwtUtil);
    }

    // --- TicketController Tests ---
    @Test
    public void testGetAllTickets() {
        Ticket ticket1 = new Ticket();
        ticket1.setId(1L);
        ticket1.setTitle("Ticket 1");

        Ticket ticket2 = new Ticket();
        ticket2.setId(2L);
        ticket2.setTitle("Ticket 2");

        TicketDTO dto1 = new TicketDTO();
        dto1.setId(1L);
        dto1.setTitle("Ticket 1");

        TicketDTO dto2 = new TicketDTO();
        dto2.setId(2L);
        dto2.setTitle("Ticket 2");

        when(ticketService.findAll()).thenReturn(Arrays.asList(ticket1, ticket2));
        when(ticketMapper.toDTO(ticket1)).thenReturn(dto1);
        when(ticketMapper.toDTO(ticket2)).thenReturn(dto2);

        List<TicketDTO> result = ticketController.getAllTickets();

        assertEquals(2, result.size());
        assertEquals("Ticket 1", result.get(0).getTitle());
        assertEquals("Ticket 2", result.get(1).getTitle());
    }

    @SuppressWarnings("null")
    @Test
    public void testGetTicketById_Found() {
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTitle("Test Ticket");

        TicketDTO dto = new TicketDTO();
        dto.setId(1L);
        dto.setTitle("Test Ticket");

        when(ticketService.findById(1L)).thenReturn(ticket);
        when(ticketMapper.toDTO(ticket)).thenReturn(dto);

        ResponseEntity<TicketDTO> response = ticketController.getTicket(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Ticket", response.getBody().getTitle());
    }

    @Test
    public void testGetTicketById_NotFound() {
        when(ticketService.findById(1L)).thenReturn(null);

        ResponseEntity<TicketDTO> response = ticketController.getTicket(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @SuppressWarnings("null")
    @Test
    public void testCreateTicket() {
        TicketCreateDTO createDTO = new TicketCreateDTO();
        createDTO.setTitle("New Ticket");
        createDTO.setCreatedById(1L);
        createDTO.setAssignedToId(2L);

        Ticket mockTicket = mock(Ticket.class);
        User creator = new User();
        creator.setId(1L);

        User assignee = new User();
        assignee.setId(2L);

        Ticket savedTicket = new Ticket();
        savedTicket.setId(1L);
        savedTicket.setTitle("New Ticket");

        TicketDTO responseDTO = new TicketDTO();
        responseDTO.setId(1L);
        responseDTO.setTitle("New Ticket");

        when(ticketMapper.toEntity(createDTO)).thenReturn(mockTicket);
        when(userService.findById(1L)).thenReturn(creator);
        when(userService.findById(2L)).thenReturn(assignee);
        when(ticketService.create(mockTicket)).thenReturn(savedTicket);
        when(ticketMapper.toDTO(savedTicket)).thenReturn(responseDTO);

        ResponseEntity<TicketDTO> response = ticketController.createTicket(createDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("New Ticket", response.getBody().getTitle());
        verify(mockTicket).setCreatedBy(creator);
        verify(mockTicket).setAssignedTo(assignee);
    }

    @Test
    public void testDeleteTicket() {
        Ticket ticket = new Ticket();
        ticket.setId(1L);

        when(ticketService.findById(1L)).thenReturn(ticket);

        ResponseEntity<Void> response = ticketController.deleteTicket(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(ticketService).delete(1L);
    }

    // --- AdminUserController Tests ---
    @SuppressWarnings("null")
    @Test
    public void testGetAllUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        when(userService.findAll()).thenReturn(Arrays.asList(user1, user2));

        ResponseEntity<List<User>> response = adminUserController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("user1", response.getBody().get(0).getUsername());
        assertEquals("user2", response.getBody().get(1).getUsername());
    }

    @SuppressWarnings("null")
    @Test
    public void testGetUserById() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userService.findById(1L)).thenReturn(user);

        ResponseEntity<User> response = adminUserController.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("testuser", response.getBody().getUsername());
    }

    @SuppressWarnings("null")
    @Test
    public void testCreateUser() {
        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn("newuser");
        when(mockUser.getPassword()).thenReturn("password");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userService.create(mockUser)).thenReturn(savedUser);

        ResponseEntity<User> response = adminUserController.createUser(mockUser);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("newuser", response.getBody().getUsername());
        verify(mockUser).setPassword("encodedPassword");
    }

    @SuppressWarnings("null")
    @Test
    public void testUpdateUser() {
        User userToUpdate = mock(User.class);
        when(userToUpdate.getId()).thenReturn(1L);
        when(userToUpdate.getUsername()).thenReturn("updateduser");
        when(userToUpdate.getPassword()).thenReturn("newpassword");
        userToUpdate.setUsername("updateduser");
        userToUpdate.setPassword("newpassword");

        User updatedUser = mock(User.class);
        when(updatedUser.getId()).thenReturn(1L);
        when(updatedUser.getUsername()).thenReturn("updateduser");
        when(updatedUser.getPassword()).thenReturn("encodedNewPassword");
        updatedUser.setId(1L);
        updatedUser.setUsername("updateduser");

        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(userService.update(userToUpdate)).thenReturn(updatedUser);

        ResponseEntity<User> response = adminUserController.updateUser(1L, userToUpdate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("updateduser", response.getBody().getUsername());
        assertEquals(1L, userToUpdate.getId());
        verify(userToUpdate).setPassword("encodedNewPassword");
    }

    @Test
    public void testDeleteUser() {
        doNothing().when(userService).delete(1L);

        ResponseEntity<Void> response = adminUserController.deleteUser(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).delete(1L);
    }

    // --- AuthController Tests ---
    @Test
    public void testAuthControllerExists() {
        assertNotNull(authController);
    }
    
    @Test
    public void testAuthLogin() {
        AuthController.AuthRequest authRequest = new AuthController.AuthRequest();
        authRequest.setUsername("admin");
        authRequest.setPassword("password");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("admin");

        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwt-token");

        ResponseEntity<?> response = authController.login(authRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof AuthController.AuthResponse);

        verify(authenticationManager).authenticate(
            any(UsernamePasswordAuthenticationToken.class)
        );
    }

    // --- Rest ---
    @SuppressWarnings("null")
    @Test
    public void testAssignTicket() {
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTitle("Test Ticket");

        TicketDTO dto = new TicketDTO();
        dto.setId(1L);
        dto.setTitle("Test Ticket");

        when(ticketService.assignToUser(1L, 2L)).thenReturn(ticket);
        when(ticketMapper.toDTO(ticket)).thenReturn(dto);

        ResponseEntity<TicketDTO> response = ticketController.assignTicket(1L, 2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Ticket", response.getBody().getTitle());
    }

    @SuppressWarnings("null")
    @Test
    public void testChangeTicketStatus() {
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setStatus(TicketStatus.CLOSED);

        TicketDTO dto = new TicketDTO();
        dto.setId(1L);
        dto.setStatus(TicketStatus.CLOSED);

        when(ticketService.changeStatus(1L, TicketStatus.CLOSED)).thenReturn(ticket);
        when(ticketMapper.toDTO(ticket)).thenReturn(dto);

        ResponseEntity<TicketDTO> response = ticketController.changeStatus(1L, "CLOSED");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TicketStatus.CLOSED, response.getBody().getStatus());
    }

    @SuppressWarnings("null")
    @Test
    public void testChangeTicketPriority() {
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setPriority(TicketPriority.HIGH);

        TicketDTO dto = new TicketDTO();
        dto.setId(1L);
        dto.setPriority(TicketPriority.HIGH);

        when(ticketService.changePriority(1L, TicketPriority.HIGH)).thenReturn(ticket);
        when(ticketMapper.toDTO(ticket)).thenReturn(dto);

        ResponseEntity<TicketDTO> response = ticketController.changePriority(1L, "HIGH");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TicketPriority.HIGH, response.getBody().getPriority());
    }

    // --- Service Mock Tests ---
    @Test
    public void testUserServiceMock() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userService.findById(1L)).thenReturn(user);

        User found = userService.findById(1L);

        assertEquals(1L, found.getId());
        assertEquals("testuser", found.getUsername());
    }

    @Test
    public void testTicketServiceMock() {
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTitle("Test Ticket");

        when(ticketService.findById(1L)).thenReturn(ticket);

        Ticket found = ticketService.findById(1L);

        assertEquals(1L, found.getId());
        assertEquals("Test Ticket", found.getTitle());
    }

    // --- Edge Cases ---
    @Test
    public void testGetTicketById_ExceptionHandling() {
        when(ticketService.findById(999L)).thenThrow(new RuntimeException("Ticket not found"));

        assertThrows(RuntimeException.class, () -> ticketController.getTicket(999L));
    }

    @SuppressWarnings("null")
    @Test
    public void testCreateTicket_WithoutAssignedUser() {
        TicketCreateDTO createDTO = new TicketCreateDTO();
        createDTO.setTitle("Simple Ticket");
        createDTO.setCreatedById(1L);

        Ticket mockTicket = mock(Ticket.class); // Mock zamiast new Ticket()
        User creator = new User();
        creator.setId(1L);

        Ticket savedTicket = new Ticket();
        savedTicket.setId(1L);
        savedTicket.setTitle("Simple Ticket");

        TicketDTO responseDTO = new TicketDTO();
        responseDTO.setId(1L);
        responseDTO.setTitle("Simple Ticket");

        when(ticketMapper.toEntity(createDTO)).thenReturn(mockTicket);
        when(userService.findById(1L)).thenReturn(creator);
        when(ticketService.create(mockTicket)).thenReturn(savedTicket);
        when(ticketMapper.toDTO(savedTicket)).thenReturn(responseDTO);

        ResponseEntity<TicketDTO> response = ticketController.createTicket(createDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Simple Ticket", response.getBody().getTitle());
        verify(mockTicket).setCreatedBy(creator);
        verify(mockTicket, never()).setAssignedTo(any());
    }
}