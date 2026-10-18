package com.pulsepass.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;

import org.junit.jupiter.api.Test;

class JwtServiceTest {

    static final String SECRET = "cHVsc2VwYXNzLXRlc3Qtc2VjcmV0LWtleS0zMmJ5dGVzISE=";
    static final String OTHER_SECRET = "b3RyYS1jbGF2ZS1kaXN0aW50YS1wYXJhLXBydWViYXMtMzJieXRlcyEh";

    private JwtService service(String issuer, String secret, Duration ttl) {
        return new JwtService(new JwtProperties(issuer, secret, ttl));
    }

    @Test
    void generateAndParse_roundTrip() {
        JwtService jwt = service("pulsepass-test", SECRET, Duration.ofMinutes(15));
        String token = jwt.generateToken(42L);
        assertThat(jwt.parseUserId(token)).contains(42L);
        assertThat(jwt.getExpiresInSeconds()).isEqualTo(900L);
    }

    @Test
    void tamperedToken_isRejected() {
        JwtService jwt = service("pulsepass-test", SECRET, Duration.ofMinutes(15));
        String token = jwt.generateToken(1L);
        int sig = token.lastIndexOf('.') + 1; // primer carácter de la firma (todos sus bits cuentan)
        char c = token.charAt(sig);
        String tampered = token.substring(0, sig) + (c == 'A' ? 'B' : 'A') + token.substring(sig + 1);
        assertThat(jwt.parseUserId(tampered)).isEmpty();
    }

    @Test
    void malformedToken_isRejected() {
        JwtService jwt = service("pulsepass-test", SECRET, Duration.ofMinutes(15));
        assertThat(jwt.parseUserId("no-es-un-jwt")).isEmpty();
        assertThat(jwt.parseUserId("")).isEmpty();
    }

    @Test
    void expiredToken_isRejected() {
        JwtService jwt = service("pulsepass-test", SECRET, Duration.ofSeconds(-30));
        assertThat(jwt.parseUserId(jwt.generateToken(1L))).isEmpty();
    }

    @Test
    void tokenSignedWithOtherKey_isRejected() {
        JwtService other = service("pulsepass-test", OTHER_SECRET, Duration.ofMinutes(15));
        JwtService jwt = service("pulsepass-test", SECRET, Duration.ofMinutes(15));
        assertThat(jwt.parseUserId(other.generateToken(1L))).isEmpty();
    }

    @Test
    void tokenWithOtherIssuer_isRejected() {
        JwtService other = service("otro-emisor", SECRET, Duration.ofMinutes(15));
        JwtService jwt = service("pulsepass-test", SECRET, Duration.ofMinutes(15));
        assertThat(jwt.parseUserId(other.generateToken(1L))).isEmpty();
    }

    @Test
    void shortSecret_isRejected() {
        assertThatThrownBy(() -> service("pulsepass-test", "c2hvcnQ=", Duration.ofMinutes(15)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void blankSecret_isRejected() {
        assertThatThrownBy(() -> service("pulsepass-test", " ", Duration.ofMinutes(15)))
                .isInstanceOf(IllegalStateException.class);
    }
}
