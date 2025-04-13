package com.essa.service;

import com.essa.model.Ticket;
import com.essa.model.TicketStatus;
import com.essa.model.TicketPriority;

import java.util.List;

public interface TicketService {
    Ticket findById(Long id);
    List<Ticket> findAll();
    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByCreatedBy(String createdBy);
    List<Ticket> findByAssignedTo(String assignedTo);
    
    Ticket create(Ticket ticket);
    Ticket update(Ticket ticket);
    void delete(Long id);
    
    Ticket assignToUser(Long ticketId, String username);
    Ticket changeStatus(Long ticketId, TicketStatus status);
    Ticket changePriority(Long ticketId, TicketPriority priority);
}