package com.essa.service.impl;

import com.essa.model.Ticket;
import com.essa.model.TicketPriority;
import com.essa.model.TicketStatus;
import com.essa.model.User;
import com.essa.repository.TicketRepository;
import com.essa.repository.UserRepository;
import com.essa.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
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

    @Override
    @Transactional
    public Ticket create(Ticket ticket) {
        ticket.setId(null);
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