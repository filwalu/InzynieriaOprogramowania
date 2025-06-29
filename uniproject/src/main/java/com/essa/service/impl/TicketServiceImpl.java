package com.essa.service.impl;

import com.essa.model.Ticket;
import com.essa.model.TicketPriority;
import com.essa.model.TicketStatus;
import com.essa.model.User;
import com.essa.repository.TicketRepository;
import com.essa.repository.UserRepository;
import com.essa.service.TicketService;
import com.essa.util.EmailMessageBuilder;
import com.essa.util.FormatValidator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.Logger;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public TicketServiceImpl(TicketRepository ticketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    public boolean validateTicketData(String email, String username) {
        FormatValidator validator = FormatValidator.getInstance();
        return validator.isValidEmail(email) && validator.isValidUsername(username);
    }

    @Override
    public Ticket findById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));
    }

    @Override
    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    @Override
    public List<Ticket> findByStatus(TicketStatus status) {
        return ticketRepository.findByStatus(status);
    }

    @Override
    public List<Ticket> findByCreatedBy(User createdBy) {
        return ticketRepository.findByCreatedBy(createdBy);
    }

    @Override
    public List<Ticket> findByAssignedTo(User assignedTo) {
        return ticketRepository.findByAssignedTo(assignedTo);
    }

    // should be moved
    public String createAssignmentEmail(String userEmail, String ticketTitle, String assigneeName) {
        return new EmailMessageBuilder()
                .to(userEmail)
                .ticketAssigned(ticketTitle, assigneeName)
                .priority("high")
                .build();
    }

    @Override
    @Transactional
    public Ticket create(Ticket ticket) {
        ticket.setId(null);

        if (ticket.getCreatedBy() != null) {
            String email = ticket.getCreatedBy().getEmail();
            String username = ticket.getCreatedBy().getUsername();

            boolean isValid = validateTicketData(email, username);
            Logger.getLogger(TicketServiceImpl.class.getName())
                    .info("Ticket data validation for email: " + email + ", username: " + username + " - Result: " + isValid);
            if (!isValid) {
                throw new RuntimeException("Invalid ticket data: email or username format is incorrect.");
            }
        } else {
            Logger.getLogger(TicketServiceImpl.class.getName())
                    .warning("Ticket created without a user. This may lead to issues with ticket assignment.");
        }
        return ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public Ticket update(Ticket ticket) {
        if (!ticketRepository.existsById(ticket.getId())) {
            throw new RuntimeException("Ticket not found with id: " + ticket.getId());
        }
        return ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new RuntimeException("Ticket not found with id: " + id);
        }
        ticketRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Ticket assignToUser(Long ticketId, Long userId) {
        Ticket ticket = findById(ticketId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        ticket.setAssignedTo(user);
        
        if (ticket.getPriority() == TicketPriority.HIGH || ticket.getPriority() == TicketPriority.CRITICAL) {
            String email = createAssignmentEmail(
                user.getEmail(),
                ticket.getTitle(),
                user.getUsername()
            );
            // Here service would send the email
            Logger.getLogger(TicketServiceImpl.class.getName())
                    .info("Sending assignment email: " + email);
        } else {
            Logger.getLogger("Email not sent for low priority ticket: " + ticket.getTitle() + ticket.getPriority());
        }
        return ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public Ticket changeStatus(Long ticketId, TicketStatus status) {
        Ticket ticket = findById(ticketId);
        ticket.setStatus(status);
        return ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public Ticket changePriority(Long ticketId, TicketPriority priority) {
        Ticket ticket = findById(ticketId);
        ticket.setPriority(priority);
        return ticketRepository.save(ticket);
    }
}