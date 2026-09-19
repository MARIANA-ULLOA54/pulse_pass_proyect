package com.pulsepass.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pulsepass.domain.Ticket;
import com.pulsepass.domain.enums.TicketStatus;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // FR-TKT-002 / AC-005
    Optional<Ticket> findByTicketCode(String ticketCode);

    // FR-TKT-006 — navega Ticket -> User -> email, con y sin estado
    List<Ticket> findByUser_Email(String email);

    List<Ticket> findByUser_EmailAndStatus(String email, TicketStatus status);

    // FR-TKT-007 / AC-008 — tickets PAID por eventCode
    @Query("""
           SELECT t FROM Ticket t
           WHERE t.event.eventCode = :eventCode
             AND t.status = 'PAID'
           """)
    List<Ticket> findPaidTicketsByEventCode(@Param("eventCode") String eventCode);

    // FR-TKT-008 / AC-008 — conteo de ventas con COUNT
    @Query("""
           SELECT COUNT(t) FROM Ticket t
           WHERE t.event.eventCode = :eventCode
             AND t.status = 'PAID'
           """)
    long countPaidTicketsByEventCode(@Param("eventCode") String eventCode);

    // FR-SRC-004 — tickets de eventos futuros, orden cronologico
    @Query("""
           SELECT t FROM Ticket t
           WHERE t.event.eventDate > :date
           ORDER BY t.event.eventDate ASC
           """)
    List<Ticket> findByEventDateAfter(@Param("date") LocalDate date);
}
