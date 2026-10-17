package com.pulsepass.service;

import org.springframework.security.access.prepost.PreAuthorize;

import com.pulsepass.dto.request.RegisterUserRequest;
import com.pulsepass.dto.response.UserResponse;

public interface UserService {

    UserResponse register(RegisterUserRequest request);

    @PreAuthorize("hasRole('ADMIN') or @authorizationService.isSelfByEmail(#email)")
    UserResponse findByEmail(String email);

    @PreAuthorize("hasRole('ADMIN') or @authorizationService.isSelfByUsername(#username)")
    UserResponse findByUsername(String username);
}
