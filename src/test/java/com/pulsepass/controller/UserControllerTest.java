package com.pulsepass.controller;

import com.pulsepass.dto.response.UserResponse;
import com.pulsepass.exception.DuplicateResourceException;
import com.pulsepass.service.UserService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    MockMvc mvc;

    @MockitoBean
    UserService userService;

    UserResponse andrea = new UserResponse(1L, "andrea", "andrea@mail.com", true,
        "Andrea", "Perez", "3000000000", "Santa Marta", LocalDate.of(1998, 5, 10));

    String validJson = """
        {"username":"andrea","email":"andrea@mail.com","firstName":"Andrea",
         "lastName":"Perez","birthDate":"1998-05-10"}""";

    @Test
    void register_valid_201() throws Exception {
        when(userService.register(any())).thenReturn(andrea);
        mvc.perform(post("/api/users").contentType(APPLICATION_JSON).content(validJson))
           .andExpect(status().isCreated()).andExpect(jsonPath("$.email").value("andrea@mail.com"));
    }

    @Test
    void register_invalidEmail_400() throws Exception {
        String json = """
            {"username":"andrea","email":"no-es-email","firstName":"A","lastName":"P","birthDate":"1998-05-10"}""";
        mvc.perform(post("/api/users").contentType(APPLICATION_JSON).content(json))
           .andExpect(status().isBadRequest()).andExpect(jsonPath("$.details.email").exists());
        verify(userService, never()).register(any());
    }

    @Test
    void register_duplicate_409() throws Exception {
        when(userService.register(any())).thenThrow(new DuplicateResourceException("Username is already taken: andrea"));
        mvc.perform(post("/api/users").contentType(APPLICATION_JSON).content(validJson))
           .andExpect(status().isConflict()).andExpect(jsonPath("$.message").value("Username is already taken: andrea"));
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