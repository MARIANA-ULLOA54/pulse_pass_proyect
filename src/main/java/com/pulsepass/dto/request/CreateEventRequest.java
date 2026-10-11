package com.pulsepass.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateEventRequest(
    String title,
    String description,
    LocalDateTime eventDate,
    Long venueId,
    Long artistId,
    BigDecimal ticketPrice,
    Integer totalCapacity

    @NotBlank(message = "Event code is required") String eventCode,
    @NotBlank(message = "Name is required") String name,
    @Size(max = 500, message = "Description is too long") String description,
    @NotNull(message = "Category is required") EventCategory category,
    @NotNull(message = "Event date is required") LocalDate eventDate,
    @NotNull(message = "Minimum age is required") @Min(value = 0, message = "Minimum age must be >= 0") Integer minimumAge,
    @NotBlank(message = "Venue code is required") String venueCode)
) {}