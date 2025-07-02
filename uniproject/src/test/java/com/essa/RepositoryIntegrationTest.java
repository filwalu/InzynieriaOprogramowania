package com.essa;

import com.essa.model.Role;
import com.essa.model.User;
import com.essa.model.Permission;
import com.essa.model.Ticket;
import com.essa.model.TicketPriority;
import com.essa.model.TicketStatus;
import com.essa.repository.RoleRepository;
import com.essa.repository.UserRepository;
import com.essa.repository.PermissionRepository;
import com.essa.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RepositoryIntegrationTest {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private TicketRepository ticketRepository;

    // --- RoleRepository ---
    @Test
    public void testSaveAndFindRoleByName() {
        Role role = new Role();
        role.setName("INTEGRATION_ROLE");
        roleRepository.save(role);

        Optional<Role> found = roleRepository.findByName("INTEGRATION_ROLE");
        assertTrue(found.isPresent());
        assertEquals("INTEGRATION_ROLE", found.get().getName());
        // Passes when a role with the given name is saved and can be found by name.
    }

    // --- PermissionRepository ---
    @Test
    public void testSaveAndFindPermissionByPermission() {
        Role role = new Role();
        role.setName("PERM_ROLE");
        roleRepository.save(role);
        
        Permission perm = new Permission();
        perm.setPermission("PERM_TEST");
        perm.setDescription("desc");
        perm.setRole(role); // Assuming a permission must be associated with a role
        permissionRepository.save(perm);

        Optional<Permission> found = permissionRepository.findByPermission("PERM_TEST");
        assertTrue(found.isPresent());
        assertEquals("PERM_TEST", found.get().getPermission());
        // Passes when a permission is saved and can be found by its permission string.
    }

    // --- UserRepository ---
    @Test
    public void testSaveAndFindUserByUsernameAndEmail() {
        Role role = new Role();
        role.setName("USER_ROLE");
        roleRepository.save(role);

        User user = new User();
        user.setUsername("integrationuser");
        user.setEmail("integration@example.com");
        user.setFirstname("Integration");
        user.setLastname("Test");
        user.setPassword("password123");
        user.setRole(role); 
        userRepository.save(user);

        Optional<User> foundByUsername = userRepository.findByUsername("integrationuser");
        Optional<User> foundByEmail = userRepository.findByEmail("integration@example.com");
        assertTrue(foundByUsername.isPresent());
        assertTrue(foundByEmail.isPresent());
        assertEquals("integrationuser", foundByUsername.get().getUsername());
        assertEquals("integration@example.com", foundByEmail.get().getEmail());
        // Passes when a user can be found by both username and email after saving.
    }

    @Test
    public void testExistsByUsernameAndEmail() {
        Role role = new Role();
        role.setName("EXISTS_ROLE");
        roleRepository.save(role);

        User user = new User();
        user.setUsername("existsuser");
        user.setEmail("exists@example.com");
        user.setFirstname("Exists");
        user.setLastname("ExistsToo");
        user.setRole(role);
        user.setPassword("password123"); // Password cant be null
        userRepository.save(user);

        assertTrue(userRepository.existsByUsername("existsuser"));
        assertTrue(userRepository.existsByEmail("exists@example.com"));
        assertFalse(userRepository.existsByUsername("notexists"));
        assertFalse(userRepository.existsByEmail("notexists@example.com"));
        // Passes when existence checks for username and email return correct results.
    }

    @Test
    public void testFindByRole() {
        Role role = new Role();
        role.setName("FIND_ROLE");
        roleRepository.save(role);

        User user = new User();
        user.setUsername("roleuser");
        user.setFirstname("Exists");
        user.setLastname("ExistsToo");
        user.setEmail("roleuser@example.com");
        user.setPassword("password123");
        user.setRole(role);
        userRepository.save(user);

        List<User> users = userRepository.findByRole(role);
        assertFalse(users.isEmpty());
        assertEquals("roleuser", users.get(0).getUsername());
        // Passes when users with a specific role can be found.
    }

    // --- TicketRepository ---
    @Test
    public void testSaveAndFindTicketById() {
        Ticket ticket = new Ticket();
        ticket.setTitle("Integration Ticket");
        ticket.setDescription("desc");
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setPriority(TicketPriority.HIGH);
        ticketRepository.save(ticket);

        Optional<Ticket> found = ticketRepository.findById(ticket.getId());
        assertTrue(found.isPresent());
        assertEquals("Integration Ticket", found.get().getTitle());
        // Passes when a ticket can be found by its ID after saving.
    }

    @Test
    public void testFindByStatus() {
        Ticket ticket = new Ticket();
        ticket.setTitle("Status Ticket");
        ticket.setStatus(TicketStatus.RESOLVED);
        ticket.setPriority(TicketPriority.LOW);
        ticketRepository.save(ticket);

        List<Ticket> tickets = ticketRepository.findByStatus(TicketStatus.RESOLVED);
        assertFalse(tickets.isEmpty());
        assertEquals(TicketStatus.RESOLVED, tickets.get(0).getStatus());
        // Passes when tickets with a specific status can be found.
    }

    @Test
    public void testFindByCreatedByAndAssignedTo() {
        Role role = new Role();
        role.setName("TICKET_ROLE");
        roleRepository.save(role);

        User creator = new User();
        creator.setUsername("creator");
        creator.setFirstname("Exists");
        creator.setLastname("ExistsToo");
        creator.setEmail("creator@example.com");
        creator.setPassword("password123");
        creator.setRole(role);
        userRepository.save(creator);

        User assignee = new User();
        assignee.setUsername("assignee");
        assignee.setFirstname("Exists");
        assignee.setLastname("ExistsToo");
        assignee.setEmail("assignee@example.com");
        assignee.setPassword("password123");
        assignee.setRole(role);
        userRepository.save(assignee);

        Ticket ticket = new Ticket();
        ticket.setTitle("Assignment Ticket");
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setPriority(TicketPriority.MEDIUM);
        ticket.setCreatedBy(creator);
        ticket.setAssignedTo(assignee);
        ticketRepository.save(ticket);

        List<Ticket> byCreator = ticketRepository.findByCreatedBy(creator);
        List<Ticket> byAssignee = ticketRepository.findByAssignedTo(assignee);

        assertFalse(byCreator.isEmpty());
        assertFalse(byAssignee.isEmpty());
        assertEquals("Assignment Ticket", byCreator.get(0).getTitle());
        assertEquals("Assignment Ticket", byAssignee.get(0).getTitle());
        // Passes when tickets can be found by both creator and assignee.
    }

}