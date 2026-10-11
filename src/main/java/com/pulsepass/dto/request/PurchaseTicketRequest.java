package com.pulsepass.dto.request;

import com.pulsepass.enums.TicketType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PurchaseTicketRequest(
    Long userId,
    Long eventId,
    Integer quantity,
    @NotBlank(message = "User email is required") @Email(message = "Email must be valid") String userEmail,
    @NotBlank(message = "Event code is required") String eventCode,
    @NotNull(message = "Ticket type is required") TicketType type
) {}