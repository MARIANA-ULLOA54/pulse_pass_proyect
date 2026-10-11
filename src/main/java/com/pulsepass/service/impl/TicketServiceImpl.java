package com.pulsepass.service.impl;

import com.pulsepass.domain.Event;
import com.pulsepass.domain.EventStatus;
import com.pulsepass.domain.Ticket;
import com.pulsepass.domain.TicketStatus;
import com.pulsepass.domain.User;
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

        if (event.getStatus() != EventStatus.PUBLISHED) {
            throw new BusinessRuleException("Event is not published");
        }

        if (event.getEventDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException("Event already took place");
        }

        int age = Period.between(user.getProfile().getBirthDate(), LocalDate.now()).getYears();
        if (event.getMinimumAge() != null && age < event.getMinimumAge()) {
            throw new BusinessRuleException("User does not meet the minimum age");
        }

        long taken = ticketRepository.countByEventIdAndStatusIn(
                event.getId(),
                List.of(TicketStatus.PAID, TicketStatus.USED)
        );

        int capacity = event.getVenue().getCapacity();
        if (taken >= capacity) {
            throw new BusinessRuleException("Venue capacity exceeded");
        }

        Ticket ticket = new Ticket();
        ticket.setTicketCode("TCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        ticket.setType(r.type());
        ticket.setPrice(BigDecimal.ZERO);
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
    @Transactional
    public TicketResponse cancel(String code) {
        Ticket ticket = getTicket(code);
        if (ticket.getStatus() != TicketStatus.PAID) {
            throw new BusinessRuleException("Only PAID tickets can be cancelled");
        }

        ticket.setStatus(TicketStatus.CANCELLED);
        return ticketMapper.toResponse(ticket);
    }

    @Override
    @Transactional
    public TicketResponse markAsUsed(String code) {
        Ticket ticket = getTicket(code);
        if (ticket.getStatus() != TicketStatus.PAID) {
            throw new BusinessRuleException("Only PAID tickets can be marked as used");
        }

        ticket.setStatus(TicketStatus.USED);
        return ticketMapper.toResponse(ticket);
    }

    private Ticket getTicket(String code) {
        return ticketRepository.findByTicketCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + code));
    }
}