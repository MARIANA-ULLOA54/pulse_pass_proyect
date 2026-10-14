package com.pulsepass.dto.response;

import com.pulsepass.domain.enums.TicketStatus;
import com.pulsepass.domain.enums.TicketType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TicketResponse(
        Long id,
        String ticketCode,
        TicketType type,
        TicketStatus status,
        BigDecimal price,
        String eventCode,
        String userEmail,
        LocalDateTime purchaseDate
) {}
