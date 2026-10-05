package com.pulsepass.mapper;

import com.pulsepass.domain.Event;
import com.pulsepass.dto.response.EventResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "venueName", source = "venue.name")
    @Mapping(target = "artistName", source = "artist.stageName")
    EventResponse toResponse(Event event);

    List<EventResponse> toResponseList(List<Event> events);
}