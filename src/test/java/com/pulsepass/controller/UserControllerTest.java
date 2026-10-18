package com.pulsepass.controller;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pulsepass.dto.response.UserResponse;
import com.pulsepass.exception.DuplicateResourceException;
import com.pulsepass.security.CustomUserDetailsService;
import com.pulsepass.security.JwtService;
import com.pulsepass.security.RestAccessDeniedHandler;
import com.pulsepass.security.RestAuthenticationEntryPoint;
import com.pulsepass.security.SecurityConfig;
import com.pulsepass.service.AuthService;
import com.pulsepass.service.UserService;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, RestAuthenticationEntryPoint.class, RestAccessDeniedHandler.class})
@TestPropertySource(properties = {
    "pulsepass.security.jwt.secret-base64=cHVsc2VwYXNzLXRlc3Qtc2VjcmV0LWtleS0zMmJ5dGVzISE=",
    "pulsepass.security.jwt.issuer=pulsepass-test",
    "pulsepass.security.jwt.access-token-ttl=PT15M"})
@WithMockUser(roles = "ADMIN")
class UserControllerTest {

    @MockitoBean
    @SuppressWarnings("unused")
    JwtService jwtService;
    @MockitoBean
    @SuppressWarnings("unused")
    CustomUserDetailsService userDetailsService;
    @MockitoBean
    UserService userService;
    @MockitoBean
    AuthService authService;

    @Autowired
    MockMvc mvc;

    UserResponse andrea = new UserResponse(1L, "andrea", "andrea@mail.com", true,
            "Andrea", "Perez", "3000000000", "Santa Marta", LocalDate.of(1998, 5, 10));

    String validJson = """
        {"username":"andrea","email":"andrea@mail.com","firstName":"Andrea",
         "lastName":"Perez","birthDate":"1998-05-10","password":"UnaFraseSeguraLarga2026!"}""";

    @Test
    void register_valid_201() throws Exception {
        when(authService.register(any())).thenReturn(andrea);
        mvc.perform(post("/api/users").contentType(APPLICATION_JSON).content(validJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("andrea@mail.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void register_invalidEmail_400() throws Exception {
        String json = """
            {"username":"andrea","email":"no-es-email","firstName":"A","lastName":"P",
             "birthDate":"1998-05-10","password":"UnaFraseSeguraLarga2026!"}""";
        mvc.perform(post("/api/users").contentType(APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.details.email").exists());
        verify(authService, never()).register(any());
    }

    @Test
    void register_shortPassword_400() throws Exception {
        String json = """
            {"username":"andrea","email":"andrea@mail.com","firstName":"A","lastName":"P",
             "birthDate":"1998-05-10","password":"corta"}""";
        mvc.perform(post("/api/users").contentType(APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.details.password").exists());
        verify(authService, never()).register(any());
    }

    @Test
    void register_duplicate_409() throws Exception {
        when(authService.register(any()))
                .thenThrow(new DuplicateResourceException("Username is already taken: andrea"));
        mvc.perform(post("/api/users").contentType(APPLICATION_JSON).content(validJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username is already taken: andrea"));
    }

    @Test
    void byEmail_200() throws Exception {
        when(userService.findByEmail("andrea@mail.com")).thenReturn(andrea);
        mvc.perform(get("/api/users/by-email").param("email", "andrea@mail.com"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.username").value("andrea"));
    }

    @Test
    void byUsername_200() throws Exception {
        when(userService.findByUsername("andrea")).thenReturn(andrea);
        mvc.perform(get("/api/users/by-username").param("username", "andrea"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.email").value("andrea@mail.com"));
    }
}
