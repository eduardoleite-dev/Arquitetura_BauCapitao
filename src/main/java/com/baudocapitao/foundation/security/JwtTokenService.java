package com.baudocapitao.foundation.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class JwtTokenService {

    private final SecretKey signingKey;
    private final long expirationMillis;

    public JwtTokenService(String secret, long expirationMillis) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("O segredo JWT não pode ser vazio");
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationMillis;
    }

    public String generateToken(String userId, String username, String name) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("name", name);
        Date issuedAt = new Date();
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(issuedAt)
                .expiration(new Date(issuedAt.getTime() + expirationMillis))
                .signWith(signingKey)
                .compact();
    }

    public boolean isValid(String token) {
        try {
            Claims claims = parse(token);
            return claims.getExpiration() != null && claims.getExpiration().after(new Date());
        } catch (RuntimeException exception) {
            return false;
        }
    }

    public String getUsername(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public String getUserId(String token) {
        return getClaim(token, claims -> claims.get("userId", String.class));
    }

    private <T> T getClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(parse(token));
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
