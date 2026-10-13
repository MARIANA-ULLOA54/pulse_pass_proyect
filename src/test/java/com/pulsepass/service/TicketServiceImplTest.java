package com.pulsepass.service;

import com.pulsepass.domain.Event;
import com.pulsepass.domain.Ticket;
import com.pulsepass.domain.User;
import com.pulsepass.domain.UserProfile;
import com.pulsepass.domain.Venue;
import com.pulsepass.domain.enums.EventStatus;
import com.pulsepass.domain.enums.TicketStatus;
import com.pulsepass.domain.enums.TicketType;
import com.pulsepass.dto.request.PurchaseTicketRequest;
import com.pulsepass.dto.response.TicketResponse;
import com.pulsepass.exception.BusinessRuleException;
import com.pulsepass.exception.ResourceNotFoundException;
import com.pulsepass.mapper.TicketMapper;
import com.pulsepass.repository.EventRepository;
import com.pulsepass.repository.TicketRepository;
import com.pulsepass.repository.UserRepository;
import com.pulsepass.service.impl.TicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock TicketRepository ticketRepository;
    @Mock EventRepository eventRepository;
    @Mock UserRepository userRepository;
    @Mock TicketMapper ticketMapper;
    @InjectMocks TicketServiceImpl ticketService;

    User andrea;
    Event event;
    PurchaseTicketRequest request =
            new PurchaseTicketRequest("andrea@mail.com", "CMF-2026", TicketType.GENERAL);

    @BeforeEach
    void setUp() {
        UserProfile profile = new UserProfile();
        profile.setBirthDate(LocalDate.now().minusYears(25));
        andrea = new User();
        andrea.setId(1L);
        andrea.setEmail("andrea@mail.com");
        andrea.setActive(true);
        andrea.setProfile(profile);

        Venue venue = new Venue();
        venue.setCapacity(3);
        event = new Event();
        event.setId(10L);
        event.setEventCode("CMF-2026");
        event.setStatus(EventStatus.PUBLISHED);
        event.setEventDate(LocalDate.now().plusDays(30));
        event.setMinimumAge(18);
        event.setVenue(venue);

        lenient().when(ticketMapper.toResponse(any())).thenReturn(
                new TicketResponse(1L, "TCK-1", TicketType.GENERAL, TicketStatus.PAID,
                        BigDecimal.ZERO, "CMF-2026", "andrea@mail.com", LocalDateTime.now()));
    }

    private void givenUserAndEvent() {
        when(userRepository.findByEmail("andrea@mail.com")).thenReturn(Optional.of(andrea));
        when(eventRepository.findByEventCode("CMF-2026")).thenReturn(Optional.of(event));
    }

    @Test
    void purchase_success_createsPaidTicket() {
        givenUserAndEvent();
        when(ticketRepository.countByEventIdAndStatusIn(anyLong(), any(Collection.class))).thenReturn(0L);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));

        TicketResponse response = ticketService.purchase(request);

        assertThat(response).isNotNull();
        assertThat(event.getStatus()).isEqualTo(EventStatus.PUBLISHED);
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void purchase_lastTicket_marksEventSoldOut() {
        givenUserAndEvent();
        when(ticketRepository.countByEventIdAndStatusIn(anyLong(), any(Collection.class))).thenReturn(2L);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));

        ticketService.purchase(request);

        assertThat(event.getStatus()).isEqualTo(EventStatus.SOLD_OUT);
    }

    @Test
    void purchase_capacityExceeded_throws() {
        givenUserAndEvent();
        when(ticketRepository.countByEventIdAndStatusIn(anyLong(), any(Collection.class))).thenReturn(3L);

        assertThatThrownBy(() -> ticketService.purchase(request))
                .isInstanceOf(BusinessRuleException.class);
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void purchase_underage_throws() {
        andrea.getProfile().setBirthDate(LocalDate.now().minusYears(17));
        givenUserAndEvent();

        assertThatThrownBy(() -> ticketService.purchase(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("minimum age");
    }

    @Test
    void purchase_inactiveUser_throws() {
        andrea.setActive(false);
        when(userRepository.findByEmail("andrea@mail.com")).thenReturn(Optional.of(andrea));

        assertThatThrownBy(() -> ticketService.purchase(request))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void purchase_unpublishedEvent_throws() {
        event.setStatus(EventStatus.DRAFT);
        givenUserAndEvent();

        assertThatThrownBy(() -> ticketService.purchase(request))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void purchase_userNotFound_throws() {
        when(userRepository.findByEmail("andrea@mail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.purchase(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void cancel_paid_becomesCancelled() {
        Ticket t = new Ticket();
        t.setStatus(TicketStatus.PAID);
        when(ticketRepository.findByTicketCode("TCK-1")).thenReturn(Optional.of(t));

        ticketService.cancel("TCK-1");

        assertThat(t.getStatus()).isEqualTo(TicketStatus.CANCELLED);
    }

    @Test
    void cancel_used_throws() {
        Ticket t = new Ticket();
        t.setStatus(TicketStatus.USED);
        when(ticketRepository.findByTicketCode("TCK-1")).thenReturn(Optional.of(t));

        assertThatThrownBy(() -> ticketService.cancel("TCK-1"))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void markAsUsed_cancelled_throws() {
        Ticket t = new Ticket();
        t.setStatus(TicketStatus.CANCELLED);
        when(ticketRepository.findByTicketCode("TCK-1")).thenReturn(Optional.of(t));

        assertThatThrownBy(() -> ticketService.markAsUsed("TCK-1"))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void markAsUsed_paid_becomesUsed() {
        Ticket t = new Ticket();
        t.setStatus(TicketStatus.PAID);
        when(ticketRepository.findByTicketCode("TCK-1")).thenReturn(Optional.of(t));

        ticketService.markAsUsed("TCK-1");

        assertThat(t.getStatus()).isEqualTo(TicketStatus.USED);
    }
}
