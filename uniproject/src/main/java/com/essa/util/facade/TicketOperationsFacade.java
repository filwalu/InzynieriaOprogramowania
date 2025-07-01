package com.essa.util.facade;

import com.essa.model.TicketPriority;
import com.essa.model.User;
import com.essa.service.TicketService;
import com.essa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class TicketOperationsFacade {
    
    @Autowired
    private TicketService ticketService;
    
    @Autowired
    private UserService userService;
    
    // FACADE - raporting
    public void generateTicketSummary(Long userId) {
        Logger.getLogger(TicketOperationsFacade.class.getName()).info("=== FACADE: Generating ticket summary ===");
        
        User user = userService.findById(userId);
        var createdTickets = ticketService.findByCreatedBy(user);
        var assignedTickets = ticketService.findByAssignedTo(user);
        
        Logger.getLogger(TicketOperationsFacade.class.getName()).info("User: " + user.getUsername());
        Logger.getLogger(TicketOperationsFacade.class.getName()).info("Created tickets: " + createdTickets.size());
        Logger.getLogger(TicketOperationsFacade.class.getName()).info("Assigned tickets: " + assignedTickets.size());
        
        long highPriorityCount = assignedTickets.stream()
                .filter(t -> t.getPriority() == TicketPriority.HIGH || 
                           t.getPriority() == TicketPriority.CRITICAL)
                .count();
        
        Logger.getLogger(TicketOperationsFacade.class.getName()).info("High priority assigned: " + highPriorityCount);
        Logger.getLogger(TicketOperationsFacade.class.getName()).info("=== FACADE: Summary completed ===");
    }
}