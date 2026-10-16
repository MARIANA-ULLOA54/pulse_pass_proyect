package com.pulsepass.service;

import com.pulsepass.domain.User;
import com.pulsepass.domain.UserCredential;
import com.pulsepass.domain.UserRole;
import com.pulsepass.dto.request.LoginRequest;
import com.pulsepass.dto.response.AuthResponse;
import com.pulsepass.dto.response.AuthenticatedUserResponse;
import com.pulsepass.repository.UserCredentialRepository;
import com.pulsepass.repository.UserRepository;
import com.pulsepass.repository.UserRoleRepository;
import com.pulsepass.security.JwtService;
import com.pulsepass.security.SecurityPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserCredentialRepository userCredentialRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request) {
        // Autenticar credenciales usando Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.identifier(), request.password())
        );

        SecurityPrincipal principal = (SecurityPrincipal) authentication.getPrincipal();
        
        // Generar token JWT con vigencia de 15 minutos (900 segundos) configurados
        String token = jwtService.generateToken(principal);

        return new AuthResponse(token, "Bearer", 900);
    }

    @Transactional
    public void registerCredentialsAndRole(Long userId, String rawPassword) {
        UserCredential credential = UserCredential.builder()
                .userId(userId)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .build();
        userCredentialRepository.save(credential);

        UserRole role = UserRole.builder()
                .userId(userId)
                .role("USER") // Rol inicial por defecto exclusivamente USER[cite: 13, 16]
                .build();
        userRoleRepository.save(role);
    }

    public AuthenticatedUserResponse getAuthenticatedUser(SecurityPrincipal principal) {
        return new AuthenticatedUserResponse(
                principal.getId(),
                principal.getUsername(),
                principal.getEmail(),
                principal.getAuthorities().stream()
                        .map(a -> a.getAuthority().replace("ROLE_", ""))
                        .toList()
        );
    }
}