package com.essa.util;

import com.essa.model.Ticket;
import com.essa.model.TicketPriority;
import com.essa.model.TicketStatus;
import com.essa.model.User;
import com.essa.service.TicketService;
import com.essa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TicketOperationsFacade {
    
    @Autowired
    private TicketService ticketService;
    
    @Autowired
    private UserService userService;
        
    // FACADE - batch operations
    public void escalateAllOpenTickets() {
        System.out.println("=== FACADE: Escalating all open tickets ===");
        
        var openTickets = ticketService.findByStatus(TicketStatus.OPEN);
        System.out.println("Found " + openTickets.size() + " open tickets");
        
        for (Ticket ticket : openTickets) {
            if (ticket.getPriority() == TicketPriority.LOW) {
                ticketService.changePriority(ticket.getId(), TicketPriority.MEDIUM);
                System.out.println("Escalated ticket #" + ticket.getId() + " from LOW to MEDIUM");
            } else if (ticket.getPriority() == TicketPriority.MEDIUM) {
                ticketService.changePriority(ticket.getId(), TicketPriority.HIGH);
                System.out.println("Escalated ticket #" + ticket.getId() + " from MEDIUM to HIGH");
            }
        }
        
        System.out.println("=== FACADE: Escalation completed ===");
    }
    
    // FACADE - raportowanie
    public void generateTicketSummary(Long userId) {
        System.out.println("=== FACADE: Generating ticket summary ===");
        
        User user = userService.findById(userId);
        var createdTickets = ticketService.findByCreatedBy(user);
        var assignedTickets = ticketService.findByAssignedTo(user);
        
        System.out.println("User: " + user.getUsername());
        System.out.println("Created tickets: " + createdTickets.size());
        System.out.println("Assigned tickets: " + assignedTickets.size());
        
        long highPriorityCount = assignedTickets.stream()
                .filter(t -> t.getPriority() == TicketPriority.HIGH || 
                           t.getPriority() == TicketPriority.CRITICAL)
                .count();
        
        System.out.println("High priority assigned: " + highPriorityCount);
        System.out.println("=== FACADE: Summary completed ===");
    }
}