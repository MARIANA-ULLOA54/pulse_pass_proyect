package com.pulsepass;

import com.pulsepass.domain.Event;
import com.pulsepass.domain.Ticket;
import com.pulsepass.domain.User;
import com.pulsepass.domain.enums.TicketStatus;
import com.pulsepass.domain.enums.TicketType;
import com.pulsepass.repository.EventRepository;
import com.pulsepass.repository.TicketRepository;
import com.pulsepass.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class TicketRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Debe encontrar boletos comprados por un usuario específico")
    void shouldFindTicketsByUser() {
        User user = new User();
        user.setEmail("comprador@example.com");
        user.setPassword("secret");
        User savedUser = userRepository.save(user);

        Event event = new Event();
        event.setTitle("Sinfónica Nacional");
        event.setEventDate(LocalDateTime.now().plusDays(5));
        Event savedEvent = eventRepository.save(event);

        Ticket ticket = new Ticket();
        ticket.setEvent(savedEvent);
        ticket.setUser(savedUser);
        ticket.setPrice(new BigDecimal("150000.00"));
        ticket.setType(TicketType.VIP);
        ticket.setStatus(TicketStatus.PURCHASED);
        ticketRepository.save(ticket);

        List<Ticket> userTickets = ticketRepository.findByUserId(savedUser.getId());

        assertThat(userTickets).hasSize(1);
        assertThat(userTickets.get(0).getType()).isEqualTo(TicketType.VIP);
    }
}