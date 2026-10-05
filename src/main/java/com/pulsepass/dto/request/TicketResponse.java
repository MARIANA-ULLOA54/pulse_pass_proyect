package com.pulsepass.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TicketResponse(
    Long id,
    String ticketCode,
    String eventTitle,
    String userEmail,
    BigDecimal pricePaid,
    LocalDateTime purchaseDate
) {}