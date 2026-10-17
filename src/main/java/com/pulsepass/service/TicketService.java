package com.pulsepass.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import com.pulsepass.dto.request.PurchaseTicketRequest;
import com.pulsepass.dto.response.TicketResponse;

public interface TicketService {

    // TicketService
    @PreAuthorize("hasRole('USER') and @authorizationService.isSelfByEmail(#request.userEmail())")
    TicketResponse purchase(PurchaseTicketRequest request);

    @PreAuthorize("hasAnyRole('STAFF','ADMIN') or @authorizationService.ownsTicket(#ticketCode)")
    TicketResponse findByCode(String ticketCode);

    @PreAuthorize("hasRole('ADMIN') or @authorizationService.isSelfByEmail(#email)")
    List<TicketResponse> findByUserEmail(String email);

    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    List<TicketResponse> findPaidTicketsByEvent(String eventCode);

    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @authorizationService.ownsTicket(#ticketCode))")
    TicketResponse cancel(String ticketCode);

    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    TicketResponse markAsUsed(String ticketCode);
}
