package com.pulsepass.service;

import com.pulsepass.dto.request.CreateEventRequest;
import com.pulsepass.dto.response.EventResponse;

import java.util.List;

public interface EventService {
    EventResponse createEvent(CreateEventRequest request);
    EventResponse findById(Long id);
    List<EventResponse> findAllActiveEvents();
    List<EventResponse> findEventsByVenue(Long venueId);
}