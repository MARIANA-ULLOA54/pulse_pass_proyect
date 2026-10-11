package com.pulsepass.dto.request;

package com.pulsepass.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateEventRequest(
    @NotBlank(message = "Title is required") String title,
    @Size(max = 500, message = "Description is too long") String description,
    @NotNull(message = "Event date is required") LocalDateTime eventDate,
    @NotNull(message = "Venue is required") Long venueId,
    @NotNull(message = "Artist is required") Long artistId,
    @NotNull(message = "Ticket price is required") BigDecimal ticketPrice,
    @NotNull(message = "Total capacity is required") @Min(value = 1, message = "Total capacity must be at least 1") Integer totalCapacity
) {}