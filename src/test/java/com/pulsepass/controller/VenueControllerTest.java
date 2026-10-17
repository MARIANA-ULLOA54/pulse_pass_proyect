package com.pulsepass.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pulsepass.dto.response.VenueResponse;
import com.pulsepass.exception.ResourceNotFoundException;
import com.pulsepass.security.CustomUserDetailsService;
import com.pulsepass.security.JwtService;
import com.pulsepass.security.RestAccessDeniedHandler;
import com.pulsepass.security.RestAuthenticationEntryPoint;
import com.pulsepass.security.SecurityConfig;
import com.pulsepass.service.VenueService;

@WebMvcTest(VenueController.class)
@Import({SecurityConfig.class, RestAuthenticationEntryPoint.class, RestAccessDeniedHandler.class})
@TestPropertySource(properties = {
    "pulsepass.security.jwt.secret-base64=cHVsc2VwYXNzLXRlc3Qtc2VjcmV0LWtleS0zMmJ5dGVzISE=",
    "pulsepass.security.jwt.issuer=pulsepass-test",
    "pulsepass.security.jwt.access-token-ttl=PT15M"})
@WithMockUser(roles = "ADMIN")
class VenueControllerTest {

    @MockitoBean
    JwtService jwtService;
    @MockitoBean
    CustomUserDetailsService userDetailsService;
    @Autowired
    MockMvc mvc;

    @MockitoBean
    VenueService venueService;

    VenueResponse venue = new VenueResponse(1L, "VEN-SMR-01", "Marina Convention Center", "Santa Marta", 3, true);

    @Test
    void findByCode_existing_200() throws Exception {
        when(venueService.findByCode("VEN-SMR-01")).thenReturn(venue);
        mvc.perform(get("/api/venues/VEN-SMR-01"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("VEN-SMR-01"));
        verify(venueService).findByCode("VEN-SMR-01");
    }

    @Test
    void findByCode_missing_404() throws Exception {
        when(venueService.findByCode("X")).thenThrow(new ResourceNotFoundException("Venue not found: X"));
        mvc.perform(get("/api/venues/X"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Venue not found: X"));
    }

    @Test
    void findActive_200() throws Exception {
        when(venueService.findActiveVenues()).thenReturn(List.of(venue));
        mvc.perform(get("/api/venues/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Marina Convention Center"));
    }

    @Test
    void unexpectedError_500_withoutInternalDetails() throws Exception {
        when(venueService.findByCode("BOOM")).thenThrow(new RuntimeException("db password leaked"));
        mvc.perform(get("/api/venues/BOOM"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Unexpected error"));
    }
}
