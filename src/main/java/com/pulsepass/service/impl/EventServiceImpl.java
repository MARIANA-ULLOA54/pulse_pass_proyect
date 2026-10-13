package com.pulsepass.service.impl;

import com.pulsepass.domain.Artist;
import com.pulsepass.domain.Event;
import com.pulsepass.domain.Venue;
import com.pulsepass.dto.request.CreateEventRequest;
import com.pulsepass.dto.response.EventResponse;
import com.pulsepass.exception.BusinessRuleException;
import com.pulsepass.exception.ResourceNotFoundException;
import com.pulsepass.mapper.EventMapper;
import com.pulsepass.repository.ArtistRepository;
import com.pulsepass.repository.EventRepository;
import com.pulsepass.repository.VenueRepository;
import com.pulsepass.service.EventService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final ArtistRepository artistRepository;
    private final EventMapper eventMapper;

    public EventServiceImpl(EventRepository eventRepository,
                            VenueRepository venueRepository,
                            ArtistRepository artistRepository,
                            EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
        this.artistRepository = artistRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    @Transactional
    public EventResponse createEvent(CreateEventRequest request) {
        if (request.eventDate() != null && request.eventDate().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("Event date cannot be in the past");
        }

        Venue venue = venueRepository.findById(request.venueId())
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found with id: " + request.venueId()));

        Artist artist = artistRepository.findById(request.artistId())
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found with id: " + request.artistId()));

        Event event = new Event();
        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setEventDate(request.eventDate());
        event.setVenue(venue);
        event.setArtist(artist);
        event.setTicketPrice(request.ticketPrice());
        event.setTotalCapacity(request.totalCapacity());
        event.setActive(true);

        Event savedEvent = eventRepository.save(event);
        return eventMapper.toResponse(savedEvent);
    }

    @Override
    public EventResponse findById(Long id) {
        return eventRepository.findById(id)
                .map(eventMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
    }

    @Override
    public List<EventResponse> findAllActiveEvents() {
        return eventMapper.toResponseList(eventRepository.findByActiveTrue());
    }

    @Override
    public List<EventResponse> findEventsByVenue(Long venueId) {
        return eventMapper.toResponseList(eventRepository.findByVenueId(venueId));
    }
}