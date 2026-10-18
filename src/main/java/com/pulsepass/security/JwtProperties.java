package com.pulsepass.security;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

/** accessTokenTtl: un número sin unidad se interpreta como segundos (900 = 15 min); también acepta PT15M. */
@ConfigurationProperties(prefix = "pulsepass.security.jwt")
public record JwtProperties(String issuer, String secretBase64,
        @DurationUnit(ChronoUnit.SECONDS) Duration accessTokenTtl) {}
