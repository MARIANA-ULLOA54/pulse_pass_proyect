package com.pulsepass.controller;

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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.pulsepass.dto.TicketResponse;
import com.pulsepass.enums.TicketStatus;
import com.pulsepass.enums.TicketType;
import com.pulsepass.exception.BusinessRuleException;
import com.pulsepass.exception.ResourceNotFoundException;
import com.pulsepass.service.TicketService;

@WebMvcTest(TicketController.class)
class TicketControllerTest {
    @Autowired
    MockMvc mvc;

    @MockitoBean
    TicketService ticketService;

    TicketResponse paid = new TicketResponse(1L, "TCK-AB12CD34", TicketType.GENERAL, TicketStatus.PAID,
            BigDecimal.ZERO, "CMF-2026", "andrea@mail.com", LocalDateTime.now());

    String purchaseJson = """
            {"userEmail":"andrea@mail.com","eventCode":"CMF-2026","type":"GENERAL"}""";

    @Test
    void purchase_valid_201() throws Exception {
        when(ticketService.purchase(any())).thenReturn(paid);
        mvc.perform(post("/api/tickets").contentType(APPLICATION_JSON).content(purchaseJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    void purchase_invalid_400() throws Exception {
        mvc.perform(post("/api/tickets").contentType(APPLICATION_JSON).content("{\"userEmail\":\"x\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.userEmail").exists());
        verify(ticketService, never()).purchase(any());
    }

    @Test
    void purchase_userNotFound_404() throws Exception {
        when(ticketService.purchase(any())).thenThrow(new ResourceNotFoundException("User not found: andrea@mail.com"));
        mvc.perform(post("/api/tickets").contentType(APPLICATION_JSON).content(purchaseJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void purchase_businessRule_409() throws Exception {
        when(ticketService.purchase(any())).thenThrow(new BusinessRuleException("User does not meet the minimum age"));
        mvc.perform(post("/api/tickets").contentType(APPLICATION_JSON).content(purchaseJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void findByCode_200() throws Exception {
        when(ticketService.findByCode("TCK-AB12CD34")).thenReturn(paid);
        mvc.perform(get("/api/tickets/TCK-AB12CD34"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketCode").value("TCK-AB12CD34"));
    }

    @Test
    void byUser_200() throws Exception {
        when(ticketService.findByUserEmail("andrea@mail.com")).thenReturn(List.of(paid));
        mvc.perform(get("/api/tickets/by-user").param("email", "andrea@mail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userEmail").value("andrea@mail.com"));
    }

    @Test
    void cancel_valid_200() throws Exception {
        when(ticketService.cancel("TCK-AB12CD34")).thenReturn(paid);
        mvc.perform(patch("/api/tickets/TCK-AB12CD34/cancel")).andExpect(status().isOk());
        verify(ticketService).cancel("TCK-AB12CD34");
    }

    @Test
    void cancel_used_409() throws Exception {
        when(ticketService.cancel("T")).thenThrow(new BusinessRuleException("Only PAID tickets can be cancelled"));
        mvc.perform(patch("/api/tickets/T/cancel")).andExpect(status().isConflict());
    }

    @Test
    void use_valid_200() throws Exception {
        when(ticketService.markAsUsed("TCK-AB12CD34")).thenReturn(paid);
        mvc.perform(patch("/api/tickets/TCK-AB12CD34/use")).andExpect(status().isOk());
    }

    @Test
    void use_cancelled_409() throws Exception {
        when(ticketService.markAsUsed("T")).thenThrow(new BusinessRuleException("Only PAID tickets can be marked as used"));
        mvc.perform(patch("/api/tickets/T/use")).andExpect(status().isConflict());
    }
}