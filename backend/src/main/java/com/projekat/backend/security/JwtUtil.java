package com.projekat.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    public String generateToken(Long userId, String username, String firstName, String lastName, String userType) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("firstName", firstName)
                .claim("lastName", lastName)
                .claim("userType", userType)
                .issuedAt(now)
                .expiration(expiresAt)
                .signWith(signingKey())
                .compact();
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
    }

    public Long requireUserId(String authHeader, String expectedUserType) {
        Claims claims = parseToken(extractToken(authHeader));

        if (!expectedUserType.equals(claims.get("userType", String.class))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This action is not allowed for this user type");
        }

        return claims.get("userId", Long.class) != null
                ? claims.get("userId", Long.class).longValue()
                : Long.valueOf(claims.get("userId").toString());
    }

    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization token is missing");
        }
        return authHeader.substring("Bearer ".length());
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
