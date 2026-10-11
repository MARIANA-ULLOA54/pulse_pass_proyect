package com.pulsepass.mapper;

import com.pulsepass.domain.Ticket;
import com.pulsepass.dto.response.TicketResponse;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(target = "eventTitle", source = "event.title")
    @Mapping(target = "userEmail", source = "user.email")
    TicketResponse toResponse(Ticket ticket);

    @InheritConfiguration(name = "toResponse")
    List<TicketResponse> toResponseList(List<Ticket> tickets);
}