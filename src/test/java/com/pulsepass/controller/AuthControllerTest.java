package com.pulsepass.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.pulsepass.dto.response.AuthResponse;
import com.pulsepass.dto.response.AuthenticatedUserResponse;
import com.pulsepass.security.CustomUserDetailsService;
import com.pulsepass.security.JwtService;
import com.pulsepass.security.RestAccessDeniedHandler;
import com.pulsepass.security.RestAuthenticationEntryPoint;
import com.pulsepass.security.SecurityConfig;
import com.pulsepass.security.SecurityPrincipal;
import com.pulsepass.service.AuthService;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, RestAuthenticationEntryPoint.class, RestAccessDeniedHandler.class})
@TestPropertySource(properties = {
    "pulsepass.security.jwt.secret-base64=cHVsc2VwYXNzLXRlc3Qtc2VjcmV0LWtleS0zMmJ5dGVzISE=",
    "pulsepass.security.jwt.issuer=pulsepass-test",
    "pulsepass.security.jwt.access-token-ttl=PT15M"})
class AuthControllerTest {

    @MockitoBean
    JwtService jwtService;
    @MockitoBean
    CustomUserDetailsService userDetailsService;
    @MockitoBean
    AuthService authService;

    @Autowired
    MockMvc mvc;

    @Test
    void login_valid_200() throws Exception {
        when(authService.login(any())).thenReturn(new AuthResponse("jwt-de-prueba", "Bearer", 900L));
        mvc.perform(post("/api/auth/login").contentType(APPLICATION_JSON)
                .content("{\"identifier\":\"andrea\",\"password\":\"UnaFraseSeguraLarga2026!\"}"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.accessToken").value("jwt-de-prueba"))
           .andExpect(jsonPath("$.tokenType").value("Bearer"))
           .andExpect(jsonPath("$.expiresIn").value(900));
    }

    @Test
    void login_missingFields_400() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(APPLICATION_JSON).content("{\"identifier\":\"\"}"))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.details.identifier").exists())
           .andExpect(jsonPath("$.details.password").exists());
        verify(authService, never()).login(any());
    }

    @Test
    void login_badCredentials_401() throws Exception {
        when(authService.login(any())).thenThrow(new BadCredentialsException("Invalid credentials"));
        mvc.perform(post("/api/auth/login").contentType(APPLICATION_JSON)
                .content("{\"identifier\":\"andrea\",\"password\":\"incorrecta\"}"))
           .andExpect(status().isUnauthorized())
           .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void me_anonymous_401() throws Exception {
        mvc.perform(get("/api/auth/me"))
           .andExpect(status().isUnauthorized())
           .andExpect(jsonPath("$.status").value(401))
           .andExpect(jsonPath("$.details").exists());
    }

    @Test
    void me_authenticated_200() throws Exception {
        SecurityPrincipal principal = new SecurityPrincipal(1L, "andrea", "hash", true,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(authService.getAuthenticatedUser(principal)).thenReturn(
                new AuthenticatedUserResponse(1L, "andrea", "andrea@mail.com", List.of("USER")));
        mvc.perform(get("/api/auth/me").with(authentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()))))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.username").value("andrea"))
           .andExpect(jsonPath("$.roles[0]").value("USER"))
           .andExpect(jsonPath("$.password").doesNotExist());
    }
}
