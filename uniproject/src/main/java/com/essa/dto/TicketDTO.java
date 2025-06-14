package com.essa.dto;

import com.essa.model.TicketPriority;
import com.essa.model.TicketStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TicketDTO {
    private Long id;

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private TicketStatus status;

    @NotNull
    private TicketPriority priority;

    
}
