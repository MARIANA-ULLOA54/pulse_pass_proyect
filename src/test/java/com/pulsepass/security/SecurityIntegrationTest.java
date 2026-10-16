package com.pulsepass.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("T-SEC-001: Consulta pública de eventos publicados por anónimo retorna 200 OK")
    void publicEventsCanBeAccessedWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/events/published"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("T-SEC-002: Usuario anónimo intentando crear un evento retorna 401 Unauthorized")
    void anonymousCannotCreateEvent() throws Exception {
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Festival de Prueba\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("T-SEC-003: Usuario con rol USER intentando crear un evento retorna 403 Forbidden")
    @WithMockUser(username = "andrea", roles = {"USER"})
    void regularUserCannotCreateEvent() throws Exception {
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Festival de Prueba\"}"))
                .andExpect(status().isForbidden());
    }
}