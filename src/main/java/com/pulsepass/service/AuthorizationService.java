package com.pulsepass.service;

import com.pulsepass.repository.TicketRepository;
import com.pulsepass.repository.UserRepository;
import com.pulsepass.security.SecurityPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Políticas de propiedad (anti-IDOR). Se usa desde @PreAuthorize como
 * @authorizationService.
 */
@Service("authorizationService")
@Transactional(readOnly = true)
public class AuthorizationService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public AuthorizationService(TicketRepository ticketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    public Optional<SecurityPrincipal> currentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityPrincipal principal) {
            return Optional.of(principal);
        }
        return Optional.empty();
    }

    public boolean isStaffOrAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STAFF") || a.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * El ticket pertenece al principal (se compara por ID persistente, nunca
     * por el email del request).
     */
    public boolean ownsTicket(String ticketCode) {
        return currentPrincipal()
                .map(p -> ticketRepository.existsByTicketCodeAndUserId(ticketCode, p.getId()))
                .orElse(false);
    }

    public boolean isSelfByEmail(String email) {
        return currentPrincipal()
                .map(p -> userRepository.existsByEmailAndId(email, p.getId()))
                .orElse(false);
    }

    public boolean isSelfByUsername(String username) {
        return currentPrincipal()
                .map(p -> userRepository.existsByUsernameAndId(username, p.getId()))
                .orElse(false);
    }
}
