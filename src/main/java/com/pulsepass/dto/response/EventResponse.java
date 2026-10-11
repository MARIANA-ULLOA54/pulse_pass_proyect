package com.pulsepass.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventResponse(
    Long id,
    String title,
    String description,
    LocalDateTime eventDate,
    String venueName,
    String artistName,
    BigDecimal ticketPrice,
    Integer totalCapacity,
    boolean active
) {}