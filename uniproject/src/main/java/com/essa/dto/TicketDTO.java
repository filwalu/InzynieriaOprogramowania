package com.essa.dto;

import com.essa.model.TicketPriority;
import com.essa.model.TicketStatus;
import lombok.Data;

@Data
public class TicketDTO {
    private Long id;
    private String title;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private Long createdById;
    private Long assignedToId;
}