package com.pulsepass.service.impl;

import com.pulsepass.domain.Event;
import com.pulsepass.domain.Ticket;
import com.pulsepass.domain.User;
import com.pulsepass.domain.enums.EventStatus;
import com.pulsepass.domain.enums.TicketStatus;
import com.pulsepass.dto.request.PurchaseTicketRequest;
import com.pulsepass.dto.response.TicketResponse;
import com.pulsepass.exception.BusinessRuleException;
import com.pulsepass.exception.ResourceNotFoundException;
import com.pulsepass.mapper.TicketMapper;
import com.pulsepass.repository.EventRepository;
import com.pulsepass.repository.TicketRepository;
import com.pulsepass.repository.UserRepository;
import com.pulsepass.service.TicketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final TicketMapper ticketMapper;

    public TicketServiceImpl(TicketRepository ticketRepository,
                             EventRepository eventRepository,
                             UserRepository userRepository,
                             TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.ticketMapper = ticketMapper;
    }

    @Override
    @Transactional
    public TicketResponse purchase(PurchaseTicketRequest r) {
        User user = userRepository.findByEmail(r.userEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + r.userEmail()));
        if (!user.isActive()) {
            throw new BusinessRuleException("Inactive users cannot purchase tickets");
        }

        Event event = eventRepository.findByEventCode(r.eventCode())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + r.eventCode()));
        if (event.getStatus() == EventStatus.SOLD_OUT) {
            throw new BusinessRuleException("Event is sold out");
        }
        if (event.getStatus() != EventStatus.PUBLISHED) {
            throw new BusinessRuleException("Event is not published");
        }
        if (event.getEventDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException("Event already took place");
        }

        Integer minimumAge = event.getMinimumAge();
        if (minimumAge != null && minimumAge > 0) {
            if (user.getProfile() == null || user.getProfile().getBirthDate() == null) {
                throw new BusinessRuleException("User birth date is required to verify the minimum age");
            }
            int age = Period.between(user.getProfile().getBirthDate(), LocalDate.now()).getYears();
            if (age < minimumAge) {
                throw new BusinessRuleException("User does not meet the minimum age");
            }
        }

        long taken = ticketRepository.countByEventIdAndStatusIn(
                event.getId(), List.of(TicketStatus.PAID, TicketStatus.USED));
        int capacity = event.getVenue().getCapacity();
        if (taken >= capacity) {
            throw new BusinessRuleException("Venue capacity exceeded");
        }

        Ticket ticket = new Ticket();
        ticket.setTicketCode("TCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        ticket.setType(r.type());
        ticket.setPrice(BigDecimal.ZERO); // El modelo actual no define precio por evento
        ticket.setStatus(TicketStatus.PAID);
        ticket.setPurchaseDate(LocalDateTime.now());
        ticket.setUser(user);
        ticket.setEvent(event);
        Ticket saved = ticketRepository.save(ticket);

        if (taken + 1 >= capacity) {
            event.setStatus(EventStatus.SOLD_OUT);
        }
        return ticketMapper.toResponse(saved);
    }

    @Override
    public TicketResponse findByCode(String ticketCode) {
        return ticketMapper.toResponse(getTicket(ticketCode));
    }

    @Override
    public List<TicketResponse> findByUserEmail(String email) {
        return ticketMapper.toResponseList(ticketRepository.findByUserEmail(email));
    }

    @Override
    public List<TicketResponse> findPaidTicketsByEvent(String eventCode) {
        return ticketMapper.toResponseList(
                ticketRepository.findByEventEventCodeAndStatus(eventCode, TicketStatus.PAID));
    }

    @Override
    @Transactional
    public TicketResponse cancel(String ticketCode) {
        Ticket ticket = getTicket(ticketCode);
        if (ticket.getStatus() != TicketStatus.PAID) {
            throw new BusinessRuleException("Only PAID tickets can be cancelled");
        }
        ticket.setStatus(TicketStatus.CANCELLED);
        return ticketMapper.toResponse(ticket);
    }

    @Override
    @Transactional
    public TicketResponse markAsUsed(String ticketCode) {
        Ticket ticket = getTicket(ticketCode);
        if (ticket.getStatus() != TicketStatus.PAID) {
            throw new BusinessRuleException("Only PAID tickets can be marked as used");
        }
        ticket.setStatus(TicketStatus.USED);
        return ticketMapper.toResponse(ticket);
    }

    private Ticket getTicket(String ticketCode) {
        return ticketRepository.findByTicketCode(ticketCode)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketCode));
    }
}
