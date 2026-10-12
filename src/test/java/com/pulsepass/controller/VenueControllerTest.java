package com.pulsepass.controller;

import com.pulsepass.domain.EventCategory;
import com.pulsepass.domain.EventStatus;
import com.pulsepass.dto.EventResponse;
import com.pulsepass.dto.EventSummaryResponse;
import com.pulsepass.exception.BusinessRuleException;
import com.pulsepass.exception.ResourceNotFoundException;
import com.pulsepass.service.EventService;
import com.pulsepass.service.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    EventService eventService;

    @MockitoBean
    TicketService ticketService;

    EventResponse event = new EventResponse(1L, "CMF-2026", "Caribbean Music Fest 2026", "desc",
            EventCategory.MUSIC, EventStatus.DRAFT, LocalDate.now().plusDays(30), 18, "VEN-SMR-01", List.of());

    EventSummaryResponse summary = new EventSummaryResponse("CMF-2026", "Caribbean Music Fest 2026",
            LocalDate.now().plusDays(30), "Marina Convention Center");

    String validJson = """
            {"eventCode":"CMF-2026","name":"Caribbean Music Fest 2026","description":"desc",
             "category":"MUSIC","eventDate":"2030-01-15","minimumAge":18,"venueCode":"VEN-SMR-01"}""";

    @Test
    void create_valid_201() throws Exception {
        when(eventService.create(any())).thenReturn(event);
        mvc.perform(post("/api/events")
                        .contentType(APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.eventCode").value("CMF-2026"));
        verify(eventService).create(any());
    }

    @Test
    void create_invalid_400_neverCallsService() throws Exception {
        mvc.perform(post("/api/events")
                        .contentType(APPLICATION_JSON)
                        .content("{\"eventCode\":\"\",\"name\":\"Fest\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details.eventCode").value("Event code is required"))
                .andExpect(jsonPath("$.details.venueCode").value("Venue code is required"));
        verify(eventService, never()).create(any());
    }

    @Test
    void findByCode_existing_200() throws Exception {
        when(eventService.findByCode("CMF-2026")).thenReturn(event);
        mvc.perform(get("/api/events/CMF-2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void findByCode_missing_404() throws Exception {
        when(eventService.findByCode("X")).thenThrow(new ResourceNotFoundException("Event not found: X"));
        mvc.perform(get("/api/events/X"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void published_200() throws Exception {
        when(eventService.findPublishedEvents()).thenReturn(List.of(summary));
        mvc.perform(get("/api/events/published"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventCode").value("CMF-2026"));
    }

    @Test
    void publish_valid_200() throws Exception {
        when(eventService.publish("CMF-2026")).thenReturn(event);
        mvc.perform(patch("/api/events/CMF-2026/publish"))
                .andExpect(status().isOk());
        verify(eventService).publish("CMF-2026");
    }

    @Test
    void publish_invalid_409() throws Exception {
        when(eventService.publish("CMF-2026")).thenThrow(new BusinessRuleException("Only DRAFT events can be published"));
        mvc.perform(patch("/api/events/CMF-2026/publish"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    void addArtist_200() throws Exception {
        when(eventService.addArtist("CMF-2026", 1L)).thenReturn(event);
        mvc.perform(post("/api/events/CMF-2026/artists/1"))
                .andExpect(status().isOk());
        verify(eventService).addArtist("CMF-2026", 1L);
    }

    @Test
    void byArtist_200() throws Exception {
        when(eventService.findByArtist("Solar Beat")).thenReturn(List.of(summary));
        mvc.perform(get("/api/events/by-artist").param("stageName", "Solar Beat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}