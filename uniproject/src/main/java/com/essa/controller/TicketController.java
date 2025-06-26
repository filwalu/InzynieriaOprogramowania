package com.essa.controller;

import com.essa.dto.TicketCreateDTO;
import com.essa.dto.TicketDTO;
import com.essa.dto.TicketUpdateDTO;
import com.essa.mapper.TicketMapper;
import com.essa.model.Ticket;
import com.essa.model.User;
import com.essa.service.TicketService;
import com.essa.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Tickets", description = "Ticket operations")
@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final UserService userService;
    private final TicketMapper ticketMapper;

    public TicketController(TicketService ticketService, UserService userService, TicketMapper ticketMapper) {
        this.ticketService = ticketService;
        this.userService = userService;
        this.ticketMapper = ticketMapper;
    }

    @GetMapping
    public List<TicketDTO> getAllTickets() {
        return ticketService.findAll().stream()
                .map(ticketMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDTO> getTicket(@PathVariable("id") Long id) {
        Ticket ticket = ticketService.findById(id);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticketMapper.toDTO(ticket));
    }

    @PostMapping
    public ResponseEntity<TicketDTO> createTicket(@RequestBody TicketCreateDTO dto) {
        Ticket ticket = ticketMapper.toEntity(dto);

        if (dto.getCreatedById() != null) {
            User createdBy = userService.findById(dto.getCreatedById());
            ticket.setCreatedBy(createdBy);
        }
        if (dto.getAssignedToId() != null) {
            User assignedTo = userService.findById(dto.getAssignedToId());
            ticket.setAssignedTo(assignedTo);
        }

        Ticket saved = ticketService.create(ticket);
        return ResponseEntity.ok(ticketMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketDTO> updateTicket(@PathVariable("id") Long id, @RequestBody TicketUpdateDTO dto) {
        Ticket ticket = ticketService.findById(id);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        ticketMapper.updateEntityFromDto(dto, ticket);

        if (dto.getAssignedToId() != null) {
            User assignedTo = userService.findById(dto.getAssignedToId());
            ticket.setAssignedTo(assignedTo);
        }

        Ticket updated = ticketService.update(ticket);
        return ResponseEntity.ok(ticketMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable("id") Long id) {
        Ticket ticket = ticketService.findById(id);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/assign/{userId}")
    public ResponseEntity<TicketDTO> assignTicket(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        Ticket ticket = ticketService.assignToUser(id, userId);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticketMapper.toDTO(ticket));
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<TicketDTO> changeStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        Ticket ticket = ticketService.changeStatus(id, com.essa.model.TicketStatus.valueOf(status));
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticketMapper.toDTO(ticket));
    }

    @PostMapping("/{id}/priority")
    public ResponseEntity<TicketDTO> changePriority(@PathVariable("id") Long id, @RequestParam("priority") String priority) {
        Ticket ticket = ticketService.changePriority(id, com.essa.model.TicketPriority.valueOf(priority));
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticketMapper.toDTO(ticket));
    }
}