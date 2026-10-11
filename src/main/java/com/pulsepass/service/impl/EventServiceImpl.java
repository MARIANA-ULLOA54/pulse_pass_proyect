package com.pulsepass.service.impl;

import com.pulsepass.domain.Artist;
import com.pulsepass.domain.Event;
import com.pulsepass.domain.EventStatus;
import com.pulsepass.domain.Venue;
import com.pulsepass.dto.request.CreateEventRequest;
import com.pulsepass.dto.response.EventResponse;
import com.pulsepass.exception.BusinessRuleException;
import com.pulsepass.exception.DuplicateResourceException;
import com.pulsepass.exception.ResourceNotFoundException;
import com.pulsepass.mapper.EventMapper;
import com.pulsepass.repository.ArtistRepository;
import com.pulsepass.repository.EventRepository;
import com.pulsepass.repository.VenueRepository;
import com.pulsepass.service.EventService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

    @Override @Transactional
public EventResponse create(CreateEventRequest r) {
    if (eventRepository.findByEventCode(r.eventCode()).isPresent())
        throw new DuplicateResourceException("Event already exists: " + r.eventCode());
    if (r.eventDate().isBefore(LocalDate.now()))
        throw new BusinessRuleException("Event date cannot be in the past");
    Venue venue = venueRepository.findByCode(r.venueCode())
        .orElseThrow(() -> new ResourceNotFoundException("Venue not found: " + r.venueCode()));
    Event e = new Event();
    e.setEventCode(r.eventCode());  e.setName(r.name());
    e.setDescription(r.description());  e.setCategory(r.category());
    e.setEventDate(r.eventDate());  e.setMinimumAge(r.minimumAge());
    e.setVenue(venue);  e.setStatus(EventStatus.DRAFT);
    return eventMapper.toResponse(eventRepository.save(e));
}

@Override @Transactional
public EventResponse publish(String code) {
    Event e = getEvent(code);
    if (e.getStatus() != EventStatus.DRAFT)
        throw new BusinessRuleException("Only DRAFT events can be published");
    e.setStatus(EventStatus.PUBLISHED);
    return eventMapper.toResponse(e);
}

@Override @Transactional
public EventResponse addArtist(String code, Long artistId) {
    Event e = getEvent(code);
    if (e.getStatus() == EventStatus.CANCELLED || e.getStatus() == EventStatus.FINISHED)
        throw new BusinessRuleException("Cannot add artists to a " + e.getStatus() + " event");
    Artist a = artistRepository.findById(artistId)
        .orElseThrow(() -> new ResourceNotFoundException("Artist not found with id: " + artistId));
    if (e.getArtists().contains(a))
        throw new BusinessRuleException("Artist already associated to the event");
    e.addArtist(a);
    return eventMapper.toResponse(e);
}

private Event getEvent(String code) {
    return eventRepository.findByEventCode(code)
        .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + code));
}
}