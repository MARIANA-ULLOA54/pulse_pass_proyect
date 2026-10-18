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
import com.pulsepass.repository.UserCredentialRepository;
import com.pulsepass.repository.UserRepository;
import com.pulsepass.repository.UserRoleRepository;
import com.pulsepass.security.JwtService;
import com.pulsepass.security.SecurityPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserService userService;
    @Mock UserRepository userRepository;
    @Mock UserCredentialRepository credentialRepository;
    @Mock UserRoleRepository roleRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock AuthenticationManager authenticationManager;
    @Mock JwtService jwtService;
    @InjectMocks AuthService authService;

    RegisterUserRequest request = new RegisterUserRequest("andrea", "andrea@mail.com", "Andrea", "Perez",
            null, null, LocalDate.of(1998, 5, 10), "UnaFraseSeguraLarga2026!");
    UserResponse created = new UserResponse(1L, "andrea", "andrea@mail.com", true, "Andrea", "Perez",
            null, null, LocalDate.of(1998, 5, 10));

    @Test
    void register_hashesPassword_andAssignsUserRole() {
        when(userService.register(request)).thenReturn(created);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$hash");

        authService.register(request);

        ArgumentCaptor<UserCredential> cred = ArgumentCaptor.forClass(UserCredential.class);
        verify(credentialRepository).save(cred.capture());
        assertThat(cred.getValue().getPasswordHash()).isEqualTo("$2a$hash")
                .isNotEqualTo(request.password());

        ArgumentCaptor<UserRole> role = ArgumentCaptor.forClass(UserRole.class);
        verify(roleRepository).save(role.capture());
        assertThat(role.getValue().getRole()).isEqualTo(Role.USER);
        assertThat(role.getValue().getUserId()).isEqualTo(1L);
    }

    @Test
    void register_passwordOver72Bytes_isRejected() {
        RegisterUserRequest longPass = new RegisterUserRequest("andrea", "andrea@mail.com", "Andrea", "Perez",
                null, null, LocalDate.of(1998, 5, 10), "a".repeat(73));

        assertThatThrownBy(() -> authService.register(longPass)).isInstanceOf(BusinessRuleException.class);
        verify(userService, never()).register(any());
    }

    @Test
    void login_success_returnsBearerToken() {
        SecurityPrincipal p = new SecurityPrincipal(1L, "andrea", "hash", true,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(p, null, p.getAuthorities()));
        when(jwtService.generateToken(1L)).thenReturn("jwt");
        when(jwtService.getExpiresInSeconds()).thenReturn(900L);

        AuthResponse response = authService.login(new LoginRequest("andrea", "x"));

        assertThat(response.accessToken()).isEqualTo("jwt");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(900L);
    }

    @Test
    void login_anyAuthenticationFailure_becomesGenericBadCredentials() {
        when(authenticationManager.authenticate(any())).thenThrow(new DisabledException("inactivo"));

        assertThatThrownBy(() -> authService.login(new LoginRequest("andrea", "x")))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid credentials");
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void getAuthenticatedUser_returnsMinimalData_withoutRolePrefix() {
        User user = new User();
        user.setId(1L);
        user.setUsername("andrea");
        user.setEmail("andrea@mail.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        SecurityPrincipal p = new SecurityPrincipal(1L, "andrea", "hash", true,
                List.of(new SimpleGrantedAuthority("ROLE_STAFF")));

        AuthenticatedUserResponse response = authService.getAuthenticatedUser(p);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("andrea@mail.com");
        assertThat(response.roles()).containsExactly("STAFF");
    }
}
