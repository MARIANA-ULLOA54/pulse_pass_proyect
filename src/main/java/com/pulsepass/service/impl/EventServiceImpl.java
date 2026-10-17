package com.pulsepass.service.impl;

import com.pulsepass.domain.Artist;
import com.pulsepass.domain.Event;
import com.pulsepass.domain.Venue;
import com.pulsepass.domain.enums.EventStatus;
import com.pulsepass.dto.request.CreateEventRequest;
import com.pulsepass.dto.response.EventResponse;
import com.pulsepass.dto.response.EventSummaryResponse;
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

import java.time.LocalDate;
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
    public EventResponse create(CreateEventRequest r) {
        if (eventRepository.findByEventCode(r.eventCode()).isPresent()) {
            throw new DuplicateResourceException("Event already exists: " + r.eventCode());
        }
        if (r.eventDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException("Event date cannot be in the past");
        }
        Venue venue = venueRepository.findByCode(r.venueCode())
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found: " + r.venueCode()));

        Event event = new Event();
        event.setEventCode(r.eventCode());
        event.setName(r.name());
        event.setDescription(r.description());
        event.setCategory(r.category());
        event.setEventDate(r.eventDate());
        event.setMinimumAge(r.minimumAge());
        event.setVenue(venue);
        event.setStatus(EventStatus.DRAFT);
        return eventMapper.toResponse(eventRepository.save(event));
    }

    @Override
    public EventResponse findByCode(String eventCode) {
        Event event = getEvent(eventCode);
        if (event.getStatus() != EventStatus.PUBLISHED && !authorizationService.isStaffOrAdmin()) {
            throw new ResourceNotFoundException("Event not found: " + eventCode); // 404: no revela borradores
        }
        return eventMapper.toResponse(event);
    }

    @Override
    public List<EventSummaryResponse> findByArtist(String stageName) {
        boolean privileged = authorizationService.isStaffOrAdmin();
        List<Event> events = eventRepository.findByArtistsStageName(stageName).stream()
                .filter(e -> privileged || e.getStatus() == EventStatus.PUBLISHED)
                .toList();
        return eventMapper.toSummaryList(events);
    }

    @Override
    public List<EventSummaryResponse> findPublishedEvents() {
        return eventMapper.toSummaryList(eventRepository.findByStatus(EventStatus.PUBLISHED));
    }

    @Override
    @Transactional
    public EventResponse publish(String eventCode) {
        Event event = getEvent(eventCode);
        if (event.getStatus() != EventStatus.DRAFT) {
            throw new BusinessRuleException("Only DRAFT events can be published");
        }
        event.setStatus(EventStatus.PUBLISHED);
        return eventMapper.toResponse(event);
    }

    @Override
    @Transactional
    public EventResponse addArtist(String eventCode, Long artistId) {
        Event event = getEvent(eventCode);
        if (event.getStatus() == EventStatus.CANCELLED || event.getStatus() == EventStatus.FINISHED) {
            throw new BusinessRuleException("Cannot add artists to a " + event.getStatus() + " event");
        }
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found with id: " + artistId));
        if (event.getArtists().contains(artist)) {
            throw new BusinessRuleException("Artist already associated to the event");
        }
        event.addArtist(artist);
        return eventMapper.toResponse(event);
    }

    private Event getEvent(String eventCode) {
        return eventRepository.findByEventCode(eventCode)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + eventCode));
    }

}
