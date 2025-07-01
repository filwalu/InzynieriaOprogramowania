package com.essa.util.decorator;

import com.essa.model.User;

public class UserWithStatsDecorator extends UserDecorator {
    private int ticketCount;
    private int assignedCount;
    
    public UserWithStatsDecorator(User user, int ticketCount, int assignedCount) {
        super(user);
        this.ticketCount = ticketCount;
        this.assignedCount = assignedCount;
    }
    
    @Override
    public String getDisplayInfo() {
        return String.format("User: %s (%s) - Created: %d tickets, Assigned: %d tickets", 
                           user.getUsername(), 
                           user.getEmail(), 
                           ticketCount, 
                           assignedCount);
    }
    
    public int getTicketCount() { return ticketCount; }
    public int getAssignedCount() { return assignedCount; }
}