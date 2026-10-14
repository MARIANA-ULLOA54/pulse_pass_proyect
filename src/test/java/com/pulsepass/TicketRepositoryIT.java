package com.pulsepass;

import com.pulsepass.domain.Event;
import com.pulsepass.domain.Ticket;
import com.pulsepass.domain.User;
import com.pulsepass.domain.Venue;
import com.pulsepass.domain.enums.EventCategory;
import com.pulsepass.domain.enums.EventStatus;
import com.pulsepass.domain.enums.TicketStatus;
import com.pulsepass.domain.enums.TicketType;
import com.pulsepass.repository.EventRepository;
import com.pulsepass.repository.TicketRepository;
import com.pulsepass.repository.UserRepository;
import com.pulsepass.repository.VenueRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Autowired
    private VenueRepository venueRepository;

    @Test
    @DisplayName("Debe encontrar tickets comprados por un usuario y los PAID de un evento")
    void shouldFindTicketsByUserAndPaidByEvent() {
        User user = new User();
        user.setUsername("comprador");
        user.setEmail("comprador@example.com");
        user = userRepository.save(user);

        Venue venue = new Venue();
        venue.setCode("VEN-TKT-01");
        venue.setName("Auditorio Nacional");
        venue.setCity("Bogotá");
        venue.setCapacity(300);
        venue = venueRepository.save(venue);

        Event event = new Event();
        event.setEventCode("EVT-TKT-01");
        event.setName("Sinfónica Nacional");
        event.setCategory(EventCategory.CULTURE);
        event.setStatus(EventStatus.PUBLISHED);
        event.setEventDate(LocalDate.now().plusDays(5));
        event.setVenue(venue);
        event = eventRepository.save(event);

        Ticket ticket = new Ticket();
        ticket.setTicketCode("TCK-IT-0001");
        ticket.setEvent(event);
        ticket.setUser(user);
        ticket.setPrice(new BigDecimal("150000.00"));
        ticket.setType(TicketType.VIP);
        ticket.setStatus(TicketStatus.PAID);
        ticket.setPurchaseDate(LocalDateTime.now());
        ticketRepository.saveAndFlush(ticket);

        List<Ticket> byUser = ticketRepository.findByUserId(user.getId());
        assertThat(byUser).hasSize(1);
        assertThat(byUser.get(0).getType()).isEqualTo(TicketType.VIP);
        assertThat(ticketRepository.findByUserEmail("comprador@example.com")).hasSize(1);
        assertThat(ticketRepository.findByEventEventCodeAndStatus("EVT-TKT-01", TicketStatus.PAID)).hasSize(1);
    }
}
