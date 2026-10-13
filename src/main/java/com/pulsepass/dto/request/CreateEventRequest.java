package com.pulsepass.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateEventRequest(
    String title,
    String description,
    LocalDateTime eventDate,
    Long venueId,
    Long artistId,
    BigDecimal ticketPrice,
    Integer totalCapacity
) {}