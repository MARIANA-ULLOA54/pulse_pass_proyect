package com.pulsepass.dto.response;

import java.time.LocalDate;

public record EventSummaryResponse(
        String eventCode,
        String name,
        LocalDate eventDate,
        String venueName
) {}