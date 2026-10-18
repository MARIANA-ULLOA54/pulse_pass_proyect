package com.pulsepass.integration;

import com.pulsepass.AbstractIntegrationTest;
import com.pulsepass.domain.enums.Role;
import com.pulsepass.dto.request.LoginRequest;
import com.pulsepass.dto.request.RegisterUserRequest;
import com.pulsepass.dto.response.AuthResponse;
import com.pulsepass.dto.response.UserResponse;
import com.pulsepass.exception.DuplicateResourceException;
import com.pulsepass.repository.UserCredentialRepository;
import com.pulsepass.repository.UserRoleRepository;
import com.pulsepass.security.JwtService;
import com.pulsepass.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class AuthFlowIT extends AbstractIntegrationTest {

    static final String PASSWORD = "UnaFraseSeguraLarga2026!";

    @Autowired AuthService authService;
    @Autowired UserCredentialRepository credentialRepository;
    @Autowired UserRoleRepository roleRepository;
    @Autowired JwtService jwtService;

    private RegisterUserRequest request(String username) {
        return new RegisterUserRequest(username, username + "@mail.com", "Test", "User",
                null, null, LocalDate.of(1998, 5, 10), PASSWORD);
    }

    @Test
    void register_storesBcryptHash_andUserRole() {
        UserResponse created = authService.register(request("andrea"));

        String hash = credentialRepository.findById(created.id()).orElseThrow().getPasswordHash();
        assertThat(hash).startsWith("$2").isNotEqualTo(PASSWORD);
        assertThat(roleRepository.findByUserId(created.id()))
                .extracting(r -> r.getRole()).containsExactly(Role.USER);
    }

    @Test
    void login_withUsernameOrEmail_returnsValidToken() {
        UserResponse created = authService.register(request("carlos"));

        AuthResponse byUsername = authService.login(new LoginRequest("carlos", PASSWORD));
        AuthResponse byEmail = authService.login(new LoginRequest("carlos@mail.com", PASSWORD));

        assertThat(jwtService.parseUserId(byUsername.accessToken())).contains(created.id());
        assertThat(jwtService.parseUserId(byEmail.accessToken())).contains(created.id());
        assertThat(byUsername.expiresIn()).isEqualTo(900);
    }

    @Test
    void login_wrongPassword_isRejected() {
        authService.register(request("laura"));

        assertThatThrownBy(() -> authService.login(new LoginRequest("laura", "otra-clave-incorrecta")))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void register_duplicateUsername_isRejected() {
        authService.register(request("miguel"));

        assertThatThrownBy(() -> authService.register(request("miguel")))
                .isInstanceOf(DuplicateResourceException.class);
    }
}
