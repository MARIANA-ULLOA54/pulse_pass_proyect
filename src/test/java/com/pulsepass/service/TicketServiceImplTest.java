package com.pulsepass.service;

import com.pulsepass.domain.Event;
import com.pulsepass.domain.Ticket;
import com.pulsepass.domain.User;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketMapper ticketMapper;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private User sampleUser;
    private Event sampleEvent;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setEmail("usuario@test.com");

        sampleEvent = new Event();
        sampleEvent.setId(10L);
        sampleEvent.setTitle("Concierto Rock");
        sampleEvent.setActive(true);
        sampleEvent.setTotalCapacity(100);
        sampleEvent.setTicketPrice(new BigDecimal("50000"));
    }

    @Test
    @DisplayName("Debe comprar boletas exitosamente cuando hay capacidad disponible")
    void purchaseTickets_Success() {
        PurchaseTicketRequest request = new PurchaseTicketRequest(1L, 10L, 2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(eventRepository.findById(10L)).thenReturn(Optional.of(sampleEvent));
        when(ticketRepository.countByEventId(10L)).thenReturn(10L);
        when(ticketRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        TicketResponse mockResponse = new TicketResponse(
                100L, "TCK-12345678", "Concierto Rock", "usuario@test.com", new BigDecimal("50000"), LocalDateTime.now()
        );
        when(ticketMapper.toResponseList(anyList())).thenReturn(List.of(mockResponse, mockResponse));

        List<TicketResponse> responses = ticketService.purchaseTickets(request);

        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(ticketRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("Debe lanzar BusinessRuleException cuando la cantidad solicitada es menor o igual a cero")
    void purchaseTickets_InvalidQuantity_ThrowsException() {
        PurchaseTicketRequest request = new PurchaseTicketRequest(1L, 10L, 0);

        assertThrows(BusinessRuleException.class, () -> ticketService.purchaseTickets(request));
        verify(ticketRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Debe lanzar BusinessRuleException cuando el evento no está activo")
    void purchaseTickets_InactiveEvent_ThrowsException() {
        sampleEvent.setActive(false);
        PurchaseTicketRequest request = new PurchaseTicketRequest(1L, 10L, 1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(eventRepository.findById(10L)).thenReturn(Optional.of(sampleEvent));

        assertThrows(BusinessRuleException.class, () -> ticketService.purchaseTickets(request));
        verify(ticketRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Debe lanzar BusinessRuleException cuando la cantidad supera la capacidad del evento")
    void purchaseTickets_ExceedsCapacity_ThrowsException() {
        PurchaseTicketRequest request = new PurchaseTicketRequest(1L, 10L, 5);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(eventRepository.findById(10L)).thenReturn(Optional.of(sampleEvent));
        when(ticketRepository.countByEventId(10L)).thenReturn(98L); // Solo quedan 2 disponibles

        assertThrows(BusinessRuleException.class, () -> ticketService.purchaseTickets(request));
        verify(ticketRepository, never()).saveAll(anyList());
    }
}