package com.essa.repository;

import com.essa.model.Ticket;
import com.essa.model.TicketStatus;
import com.essa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByCreatedBy(User createdBy);
    List<Ticket> findByAssignedTo(User assignedTo);
}