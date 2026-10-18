package com.pulsepass.service;

import com.pulsepass.domain.User;
import com.pulsepass.domain.UserCredential;
import com.pulsepass.domain.UserRole;
import com.pulsepass.domain.enums.Role;
import com.pulsepass.dto.request.LoginRequest;
import com.pulsepass.dto.request.RegisterUserRequest;
import com.pulsepass.dto.response.AuthResponse;
import com.pulsepass.dto.response.AuthenticatedUserResponse;
import com.pulsepass.dto.response.UserResponse;
import com.pulsepass.exception.BusinessRuleException;
import com.pulsepass.exception.ResourceNotFoundException;
import com.pulsepass.repository.UserCredentialRepository;
import com.pulsepass.repository.UserRepository;
import com.pulsepass.repository.UserRoleRepository;
import com.pulsepass.security.JwtService;
import com.pulsepass.security.SecurityPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final UserCredentialRepository credentialRepository;
    private final UserRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserService userService,
            UserRepository userRepository,
            UserCredentialRepository credentialRepository,
            UserRoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /** Usuario + hash + ROLE_USER en una sola transacción (si algo falla, no queda nada). */
    @Transactional
    public UserResponse register(RegisterUserRequest request) {
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new BusinessRuleException("Password must not exceed 72 bytes");
        }
        UserResponse created = userService.register(request);
        credentialRepository.save(new UserCredential(
                created.id(), passwordEncoder.encode(request.password()), LocalDateTime.now()));
        roleRepository.save(new UserRole(created.id(), Role.USER));
        return created;
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.identifier(), request.password()));
            SecurityPrincipal principal = (SecurityPrincipal) auth.getPrincipal();
            return new AuthResponse(jwtService.generateToken(principal.getId()),
                    "Bearer", jwtService.getExpiresInSeconds());
        } catch (AuthenticationException e) {
            // Mensaje genérico: no revela si el usuario existe, está inactivo o la clave falló
            throw new BadCredentialsException("Invalid credentials");
        }
    }

    /** Datos mínimos del usuario autenticado (GET /api/auth/me). */
    @Transactional(readOnly = true)
    public AuthenticatedUserResponse getAuthenticatedUser(SecurityPrincipal principal) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + principal.getId()));
        List<String> roles = principal.getAuthorities().stream()
                .map(a -> a.getAuthority().replaceFirst("^ROLE_", ""))
                .toList();
        return new AuthenticatedUserResponse(user.getId(), user.getUsername(), user.getEmail(), roles);
    }
}
