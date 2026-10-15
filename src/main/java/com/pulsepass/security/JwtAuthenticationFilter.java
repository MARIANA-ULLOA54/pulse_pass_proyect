package com.pulsepass.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            // Extraer subject (id de usuario) de los claims
            String userIdStr = jwtService.parseAllClaims(jwt).getSubject();

            if (userIdStr != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Como guardamos el id en el sub, podemos cargar al usuario por ID o username. 
                // Aquí usamos CustomUserDetailsService cargando por el identificador correspondiente.
                // Una optimización es buscar por ID directamente, o asegurar que el service soporte la carga.
                // Para simplificar, supongamos que el subject es el email o username, o ajustamos CustomUserDetailsService.
            }
        } catch (Exception e) {
            // Token inválido o expirado; no autenticamos y dejamos que la cadena devuelva 401 si la ruta es protegida
        }

        filterChain.doFilter(request, response);
    }
}