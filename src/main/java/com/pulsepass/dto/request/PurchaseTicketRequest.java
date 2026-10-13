package com.pulsepass.dto.request;

public record PurchaseTicketRequest(
    Long userId,
    Long eventId,
    Integer quantity
) {}