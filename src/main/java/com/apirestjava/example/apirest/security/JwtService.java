package com.apirestjava.example.apirest.security;

import com.apirestjava.example.apirest.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtService {

    private final SecretKey key;
    private final long accessExpirationMs;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.access-expiration-ms:900000}") long accessExpirationMs) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessExpirationMs = accessExpirationMs;
    }

    public String generateAccessToken(User user) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .issuedAt(new Date(now))
                .expiration(new Date(now + accessExpirationMs))
                .signWith(key)
                .compact();
    }

    // Vérifie la signature et l'expiration, lève une JwtException sinon
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Lecture robuste : le nombre peut être désérialisé en Integer ou en Long
    public Long extractUserId(Claims claims) {
        Object raw = claims.get("userId");
        return raw instanceof Number number ? number.longValue() : null;
    }

    public long getAccessExpirationSeconds() {
        return accessExpirationMs / 1000;
    }
}