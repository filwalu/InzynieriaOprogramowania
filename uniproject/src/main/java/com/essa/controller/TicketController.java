package com.essa.controller;

import com.essa.dto.TicketDTO;
import com.essa.model.Ticket;
import com.essa.model.TicketPriority;
import com.essa.model.TicketStatus;
import com.essa.model.User;
import com.essa.service.TicketService;
import com.essa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final UserService userService;

    @Autowired
    public TicketController(TicketService ticketService, UserService userService) {
        this.ticketService = ticketService;
        this.userService = userService;
    }

    @GetMapping
    public List<TicketDTO> getAllTickets() {
        return ticketService.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TicketDTO getTicket(@PathVariable Long id) {
        return toDTO(ticketService.findById(id));
    }

    @PostMapping
    public TicketDTO createTicket(@RequestBody TicketDTO dto) {
        Ticket ticket = fromDTO(dto);
        return toDTO(ticketService.create(ticket));
    }

    @PutMapping("/{id}")
    public TicketDTO updateTicket(@PathVariable Long id, @RequestBody TicketDTO dto) {
        Ticket ticket = fromDTO(dto);
        ticket.setId(id);
        return toDTO(ticketService.update(ticket));
    }

    @DeleteMapping("/{id}")
    public void deleteTicket(@PathVariable Long id) {
        ticketService.delete(id);
    }

    @PostMapping("/{id}/assign/{userId}")
    public TicketDTO assignTicket(@PathVariable Long id, @PathVariable Long userId) {
        return toDTO(ticketService.assignToUser(id, userId));
    }

    @PostMapping("/{id}/status")
    public TicketDTO changeStatus(@PathVariable Long id, @RequestParam TicketStatus status) {
        return toDTO(ticketService.changeStatus(id, status));
    }

    @PostMapping("/{id}/priority")
    public TicketDTO changePriority(@PathVariable Long id, @RequestParam TicketPriority priority) {
        return toDTO(ticketService.changePriority(id, priority));
    }

    // --- Mapping DTO <-> Entity ---

    private TicketDTO toDTO(Ticket ticket) {
        TicketDTO dto = new TicketDTO();
        dto.setId(ticket.getId());
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());
        dto.setStatus(ticket.getStatus());
        dto.setPriority(ticket.getPriority());
        dto.setCreatedById(ticket.getCreatedBy() != null ? ticket.getCreatedBy().getId() : null);
        dto.setAssignedToId(ticket.getAssignedTo() != null ? ticket.getAssignedTo().getId() : null);
        return dto;
    }

    private Ticket fromDTO(TicketDTO dto) {
        Ticket ticket = new Ticket();
        ticket.setId(dto.getId());
        ticket.setTitle(dto.getTitle());
        ticket.setDescription(dto.getDescription());
        ticket.setStatus(dto.getStatus());
        ticket.setPriority(dto.getPriority());
        if (dto.getCreatedById() != null) {
            User createdBy = userService.findById(dto.getCreatedById());
            ticket.setCreatedBy(createdBy);
        }
        if (dto.getAssignedToId() != null) {
            User assignedTo = userService.findById(dto.getAssignedToId());
            ticket.setAssignedTo(assignedTo);
        }
        return ticket;
    }
}