package com.pulsepass.dto.response;
package com.pulsepass.dto.response;
import java.time.LocalDate;
import java.util.List;

public record EventResponse(
    Long id,
    String eventCode,
    String name,
    String description,
    String category,
    String status,
    LocalDate eventDate,
    Integer minimumAge,
    String venueCode,
    List<String> artistNames
) {}