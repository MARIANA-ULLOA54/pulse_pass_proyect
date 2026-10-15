package com.pulsepass.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final SecretKey key;
    private final String issuer;
    private final long accessTokenTtl;

    public JwtService(
            @Value("${pulsepass.security.jwt.secret-base64}") String secretBase64,
            @Value("${pulsepass.security.jwt.issuer}") String issuer,
            @Value("${pulsepass.security.jwt.access-token-ttl}") long accessTokenTtl) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretBase64));
        this.issuer = issuer;
        this.accessTokenTtl = accessTokenTtl;
    }

    public String generateToken(SecurityPrincipal principal) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (accessTokenTtl * 1000));

        return Jwts.builder()
                .subject(principal.getId().toString())
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiryDate)
                .id(UUID.randomUUID().toString())
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public Claims parseAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            Claims claims = parseAllClaims(token);
            String subject = claims.getSubject();
            // Validar que el subject coincida con el ID del usuario y que no esté expirado
            if (userDetails instanceof SecurityPrincipal principal) {
                return subject.equals(principal.getId().toString()) && !claims.getExpiration().before(new Date());
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}