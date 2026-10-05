package com.pulsepass.service.impl;

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
import com.pulsepass.service.TicketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public List<TicketResponse> purchaseTickets(PurchaseTicketRequest request) {
        if (request.quantity() == null || request.quantity() <= 0) {
            throw new BusinessRuleException("Quantity must be greater than zero");
        }

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.userId()));

        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + request.eventId()));

        if (!event.isActive()) {
            throw new BusinessRuleException("Cannot purchase tickets for an inactive event");
        }

        long soldTickets = ticketRepository.countByEventId(event.getId());
        if (soldTickets + request.quantity() > event.getTotalCapacity()) {
            throw new BusinessRuleException("Not enough tickets available. Capacity exceeded");
        }

        List<Ticket> ticketsToSave = new ArrayList<>();
        for (int i = 0; i < request.quantity(); i++) {
            Ticket ticket = new Ticket();
            ticket.setTicketCode("TCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            ticket.setUser(user);
            ticket.setEvent(event);
            ticket.setPricePaid(event.getTicketPrice());
            ticket.setPurchaseDate(LocalDateTime.now());
            ticketsToSave.add(ticket);
        }

        List<Ticket> savedTickets = ticketRepository.saveAll(ticketsToSave);
        return ticketMapper.toResponseList(savedTickets);
    }

    @Override
    public List<TicketResponse> findTicketsByUser(Long userId) {
        return ticketMapper.toResponseList(ticketRepository.findByUserId(userId));
    }

    @Override
    public List<TicketResponse> findTicketsByEvent(Long eventId) {
        return ticketMapper.toResponseList(ticketRepository.findByEventId(eventId));
    }
}