package com.pulsepass.dto.response;

import java.time.LocalDateTime;

public record ErrorResponse(LocalDateTime timestamp, int status, String error,
                            String message, Map<String, String> details) {}
                            