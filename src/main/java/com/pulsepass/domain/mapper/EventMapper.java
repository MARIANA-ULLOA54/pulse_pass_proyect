package com.pulsepass.mapper;

import com.pulsepass.domain.Event;
import com.pulsepass.dto.response.EventResponse;
import com.pulsepass.dto.response.EventSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "venueCode", source = "venue.code")
    @Mapping(target = "artistNames", expression =
        "java(event.getArtists().stream().map(a -> a.getStageName()).sorted().toList())")
    EventResponse toResponse(Event event);

    @Mapping(target = "venueName", source = "venue.name")
    EventSummaryResponse toSummary(Event event);
    List<EventSummaryResponse> toSummaryList(List<Event> events);
}