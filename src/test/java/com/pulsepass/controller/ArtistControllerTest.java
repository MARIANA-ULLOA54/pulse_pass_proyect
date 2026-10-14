package com.pulsepass.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.pulsepass.dto.response.ArtistResponse;
import com.pulsepass.exception.ResourceNotFoundException;
import com.pulsepass.service.ArtistService;

@WebMvcTest(ArtistController.class)
class ArtistControllerTest {
    @Autowired
    MockMvc mvc;

    @MockitoBean
    ArtistService artistService;

    ArtistResponse solar = new ArtistResponse(1L, "Solar Beat", "Electronic", true);

    @Test
    void findById_200() throws Exception {
        when(artistService.findById(1L)).thenReturn(solar);
        mvc.perform(get("/api/artists/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stageName").value("Solar Beat"));
    }

    @Test
    void findById_404() throws Exception {
        when(artistService.findById(99L))
            .thenThrow(new ResourceNotFoundException("Artist not found with id: 99"));
        mvc.perform(get("/api/artists/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void byStageName_200() throws Exception {
        when(artistService.findByStageName("Solar Beat")).thenReturn(solar);
        mvc.perform(get("/api/artists/by-stage-name").param("stageName", "Solar Beat"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.genre").value("Electronic"));
    }

    @Test
    void active_200() throws Exception {
        when(artistService.findActiveArtists()).thenReturn(List.of(solar));
        mvc.perform(get("/api/artists/active"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].active").value(true));
    }
}