package com.pulsepass.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pulsepass.dto.response.ArtistResponse;
import com.pulsepass.exception.ResourceNotFoundException;
import com.pulsepass.security.CustomUserDetailsService;
import com.pulsepass.security.JwtService;
import com.pulsepass.security.RestAccessDeniedHandler;
import com.pulsepass.security.RestAuthenticationEntryPoint;
import com.pulsepass.security.SecurityConfig;
import com.pulsepass.service.ArtistService;

@WebMvcTest(ArtistController.class)
@MockitoBean(types = {JwtService.class, CustomUserDetailsService.class})
@Import({SecurityConfig.class, RestAuthenticationEntryPoint.class, RestAccessDeniedHandler.class})
@TestPropertySource(properties = {
    "pulsepass.security.jwt.secret-base64=cHVsc2VwYXNzLXRlc3Qtc2VjcmV0LWtleS0zMmJ5dGVzISE=",
    "pulsepass.security.jwt.issuer=pulsepass-test",
    "pulsepass.security.jwt.access-token-ttl=PT15M"})
@WithMockUser(roles = "ADMIN")
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
