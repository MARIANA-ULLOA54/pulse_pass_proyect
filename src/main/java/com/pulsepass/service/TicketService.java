package com.pulsepass.service;

import com.pulsepass.dto.request.PurchaseTicketRequest;
import com.pulsepass.dto.response.TicketResponse;

import java.util.List;

public interface TicketService {
    List<TicketResponse> purchaseTickets(PurchaseTicketRequest request);
    List<TicketResponse> findTicketsByUser(Long userId);
    List<TicketResponse> findTicketsByEvent(Long eventId);
}