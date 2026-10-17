package com.pulsepass.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer ";

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        boolean singleHeader = Collections.list(request.getHeaders(HttpHeaders.AUTHORIZATION)).size() == 1;

        if (singleHeader
                && request.getHeader(HttpHeaders.AUTHORIZATION).startsWith(BEARER)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = request.getHeader(HttpHeaders.AUTHORIZATION).substring(BEARER.length()).trim();
            jwtService.parseUserId(token).ifPresent(userId -> authenticate(userId, request));
        }
        // Sin token o con token inválido: no se autentica; la cadena decide (público o 401).
        chain.doFilter(request, response);
    }

    private void authenticate(Long userId, HttpServletRequest request) {
        try {
            SecurityPrincipal principal = userDetailsService.loadUserById(userId);
            if (!principal.isEnabled()) {
                return; // usuario desactivado: el token deja de servir
            }
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    principal, null, principal.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (UsernameNotFoundException ignored) {
            // usuario eliminado o sin credenciales: no se autentica
        }
    }
}
