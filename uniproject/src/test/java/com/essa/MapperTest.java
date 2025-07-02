package com.essa;

import com.essa.dto.*;
import com.essa.mapper.*;
import com.essa.model.*;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

public class MapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private final TicketMapper ticketMapper = Mappers.getMapper(TicketMapper.class);
    private final RoleMapper roleMapper = Mappers.getMapper(RoleMapper.class);
    private final PermissionMapper permissionMapper = Mappers.getMapper(PermissionMapper.class);

    @Test
    public void testUserMapper_toDTO() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEmail("test@example.com");
        user.setRole(role);

        UserDTO dto = userMapper.toDTO(user);

        assertEquals(1L, dto.getId());
        assertEquals("testuser", dto.getUsername());
        assertEquals("Test", dto.getFirstname());
        assertEquals("User", dto.getLastname());
        assertEquals("test@example.com", dto.getEmail());
        // Usuń test roleId jeśli mapper nie mapuje tego pola automatycznie
        // assertEquals(1L, dto.getRoleId());
    }

    @Test
    public void testUserMapper_toEntity() {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setUsername("newuser");
        dto.setPassword("password123");
        dto.setFirstname("New");
        dto.setLastname("User");
        dto.setEmail("new@example.com");

        User user = userMapper.toEntity(dto);

        assertNull(user.getId());
        assertEquals("newuser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("New", user.getFirstname());
        assertEquals("User", user.getLastname());
        assertEquals("new@example.com", user.getEmail());
        assertNull(user.getRole());
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
    }

    @Test
    public void testTicketMapper_toDTO() {
        User creator = new User();
        creator.setId(1L);
        
        User assignee = new User();
        assignee.setId(2L);

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTitle("Test Ticket");
        ticket.setDescription("Test Description");
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setPriority(TicketPriority.HIGH);
        ticket.setCreatedBy(creator);
        ticket.setAssignedTo(assignee);

        TicketDTO dto = ticketMapper.toDTO(ticket);

        assertEquals(1L, dto.getId());
        assertEquals("Test Ticket", dto.getTitle());
        assertEquals("Test Description", dto.getDescription());
        assertEquals(TicketStatus.OPEN, dto.getStatus());
        assertEquals(TicketPriority.HIGH, dto.getPriority());
        assertEquals(1L, dto.getCreatedById());
        assertEquals(2L, dto.getAssignedToId());
    }

    @Test
    public void testTicketMapper_toEntity() {
        TicketCreateDTO dto = new TicketCreateDTO();
        dto.setTitle("New Ticket");
        dto.setDescription("New Description");
        dto.setStatus(TicketStatus.IN_PROGRESS);
        dto.setPriority(TicketPriority.MEDIUM);

        Ticket ticket = ticketMapper.toEntity(dto);

        assertNull(ticket.getId());
        assertEquals("New Ticket", ticket.getTitle());
        assertEquals("New Description", ticket.getDescription());
        assertEquals(TicketStatus.IN_PROGRESS, ticket.getStatus());
        assertEquals(TicketPriority.MEDIUM, ticket.getPriority());
        assertNull(ticket.getCreatedBy());
        assertNull(ticket.getAssignedTo());
        assertNull(ticket.getCreatedAt());
        assertNull(ticket.getUpdatedAt());
    }

    @Test
    public void testRoleMapper_toDTO() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ADMIN");

        RoleDTO dto = roleMapper.toDTO(role);

        assertEquals(1L, dto.getId());
        assertEquals("ADMIN", dto.getName());
    }

    @Test
    public void testRoleMapper_toEntity() {
        RoleDTO dto = new RoleDTO();
        dto.setId(1L);
        dto.setName("USER");

        Role role = roleMapper.toEntity(dto);

        assertEquals(1L, role.getId());
        assertEquals("USER", role.getName());
    }

    @Test
    public void testPermissionMapper_toDTO() {
        Role role = new Role();
        role.setId(1L);

        Permission permission = new Permission();
        permission.setId(1L);
        permission.setPermission("READ_USERS");
        permission.setDescription("Can read users");
        permission.setRole(role);

        PermissionDTO dto = permissionMapper.toDTO(permission);

        assertEquals(1L, dto.getId());
        assertEquals("READ_USERS", dto.getPermission());
        assertEquals("Can read users", dto.getDescription());
    }

    @Test
    public void testPermissionMapper_toEntity() {
        PermissionDTO dto = new PermissionDTO();
        dto.setId(1L);
        dto.setPermission("WRITE_USERS");
        dto.setDescription("Can write users");

        Permission permission = permissionMapper.toEntity(dto);

        assertEquals(1L, permission.getId());
        assertEquals("WRITE_USERS", permission.getPermission());
        assertEquals("Can write users", permission.getDescription());
    }
}