package com.pulsepass.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import com.pulsepass.dto.request.CreateEventRequest;
import com.pulsepass.dto.response.EventResponse;
import com.pulsepass.dto.response.EventSummaryResponse;

public interface EventService {

    EventResponse findByCode(String eventCode);

    List<EventSummaryResponse> findPublishedEvents();

    List<EventSummaryResponse> findByArtist(String stageName);

    @PreAuthorize("hasRole('ADMIN')")
    EventResponse create(CreateEventRequest request);

    @PreAuthorize("hasRole('ADMIN')")
    EventResponse publish(String eventCode);

    @PreAuthorize("hasRole('ADMIN')")
    EventResponse addArtist(String eventCode, Long artistId);
}
