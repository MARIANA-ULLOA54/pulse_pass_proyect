package com.pulsepass.security;

import com.pulsepass.domain.Ticket;
import com.pulsepass.domain.User;
import com.pulsepass.exception.BusinessRuleException;
import com.pulsepass.repository.TicketRepository;
import com.pulsepass.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    /**
     * Verifica que el usuario autenticado (USER) solo compre tickets usando su propio correo.
     */
    public void validateTicketPurchaseOwnership(String requestEmail, SecurityPrincipal principal) {
        // Si el usuario es ADMIN o STAFF, puede tener consideraciones especiales, 
        // pero para USER exigimos que el email coincida con el principal.
        boolean isUserRole = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));

        if (isUserRole && !principal.getEmail().equalsIgnoreCase(requestEmail)) {
            throw new AccessDeniedException("No puede comprar tickets para un correo diferente al de su cuenta autenticada");
        }
    }

    /**
     * Verifica que el ticket pertenezca al usuario autenticado antes de consultarlo o cancelarlo.
     */
    public Ticket validateTicketOwnership(String ticketCode, SecurityPrincipal principal) {
        Ticket ticket = ticketRepository.findByTicketCode(ticketCode)
                .orElseThrow(() -> new BusinessRuleException("Ticket no encontrado con código: " + ticketCode));

        boolean isPrivileged = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STAFF") || a.getAuthority().equals("ROLE_ADMIN"));

        if (isPrivileged) {
            return ticket; // STAFF y ADMIN autorizado para operaciones operativas[cite: 13, 14]
        }

        // Validar propiedad estricta por ID de usuario
        if (!ticket.getUser().getId().equals(principal.getId())) {
            throw new AccessDeniedException("No tiene permisos para acceder o modificar este ticket ajeno");
        }

        return ticket;
    }
}