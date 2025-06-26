package com.essa.mapper;

import com.essa.dto.TicketCreateDTO;
import com.essa.dto.TicketDTO;
import com.essa.dto.TicketUpdateDTO;
import com.essa.model.Ticket;
import com.essa.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "assignedToId", source = "assignedTo.id")
    TicketDTO toDTO(Ticket ticket);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "assignedTo", ignore = true)
    Ticket toEntity(TicketCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "assignedTo", ignore = true)
    void updateEntityFromDto(TicketUpdateDTO dto, @MappingTarget Ticket ticket);

    default void setCreatedBy(Ticket ticket, User user) {
        ticket.setCreatedBy(user);
    }

    default void setAssignedTo(Ticket ticket, User user) {
        ticket.setAssignedTo(user);
    }
}