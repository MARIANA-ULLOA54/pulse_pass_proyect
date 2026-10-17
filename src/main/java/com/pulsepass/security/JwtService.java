package com.pulsepass.security;

import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class JwtService {

    private static final String EXPECTED_ALGORITHM = "HS256";

    private final SecretKey key;
    private final String issuer;
    private final Duration ttl;

    public JwtService(JwtProperties props) {
        if (props.secretBase64() == null || props.secretBase64().isBlank()) {
            throw new IllegalStateException("PULSEPASS_JWT_SECRET_BASE64 is required");
        }
        byte[] bytes;
        try {
            bytes = Decoders.BASE64.decode(props.secretBase64());
        } catch (RuntimeException e) {
            throw new IllegalStateException("JWT secret is not valid Base64");
        }
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 bytes");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.issuer = props.issuer();
        this.ttl = props.accessTokenTtl();
    }

    public String generateToken(Long userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(ttl)))
                .id(UUID.randomUUID().toString())
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Devuelve el id de usuario si el token es válido; vacío en cualquier otro
     * caso.
     */
    public Optional<Long> parseUserId(String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer(issuer)
                    .build()
                    .parseSignedClaims(token);

            if (!EXPECTED_ALGORITHM.equals(jws.getHeader().getAlgorithm())) {
                return Optional.empty();
            }
            Claims claims = jws.getPayload();
            if (claims.getSubject() == null || claims.getSubject().isBlank()
                    || claims.getExpiration() == null || claims.getId() == null) {
                return Optional.empty();
            }
            return Optional.of(Long.valueOf(claims.getSubject()));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public long getExpiresInSeconds() {
        return ttl.toSeconds();
    }
}
