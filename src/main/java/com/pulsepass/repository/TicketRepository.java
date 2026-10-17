package com.pulsepass.repository;

import com.pulsepass.domain.Ticket;
import com.pulsepass.domain.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUserId(Long userId);
    List<Ticket> findByEventId(Long eventId);
    long countByEventId(Long eventId);
    Optional<Ticket> findByTicketCode(String ticketCode);
    boolean existsByTicketCodeAndUser_Id(String ticketCode, Long userId);
    List<Ticket> findByUserEmail(String email);
    List<Ticket> findByEventEventCodeAndStatus(String eventCode, TicketStatus status);
    long countByEventIdAndStatusIn(Long eventId, Collection<TicketStatus> statuses);
}
