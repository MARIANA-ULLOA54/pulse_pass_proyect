package com.pulsepass.dto.response;

import com.pulsepass.enums.TicketStatus;
import com.pulsepass.enums.TicketType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TicketResponse(
    Long id,
    String ticketCode,
    String eventTitle,
    TicketType type,
    TicketStatus status,
    BigDecimal price,
    String eventCode,
    String userEmail,
    LocalDateTime purchaseDate
) {}

