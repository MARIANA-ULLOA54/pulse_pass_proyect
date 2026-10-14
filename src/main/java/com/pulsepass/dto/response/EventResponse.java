package com.pulsepass.dto.response;

import com.pulsepass.domain.enums.EventCategory;
import com.pulsepass.domain.enums.EventStatus;

import java.time.LocalDate;
import java.util.List;

public record EventResponse(
        Long id,
        String eventCode,
        String name,
        String description,
        EventCategory category,
        EventStatus status,
        LocalDate eventDate,
        Integer minimumAge,
        String venueCode,
        List<String> artistNames
) {}
