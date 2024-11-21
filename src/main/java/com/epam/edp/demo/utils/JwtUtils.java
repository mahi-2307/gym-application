package com.epam.edp.demo.utils;

import io.jsonwebtoken.JwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class JwtUtils {
    private final JwtDecoder jwtDecoder;

    public JwtUtils(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    public String extractEmail(String token) {
        try {
            Jwt decodedJwt = jwtDecoder.decode(token);
            return decodedJwt.getClaimAsString("email");
        } catch (JwtException e) {
            throw new IllegalStateException("Invalid token");
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Jwt decodedJwt = jwtDecoder.decode(token);
            return decodedJwt.getExpiresAt().isBefore(Instant.now());
        } catch (JwtException e) {
            return true;
        }
    }
    public String extractRole(String token) {
        try {
            Jwt decodedJwt = jwtDecoder.decode(token);
            return decodedJwt.getClaimAsString("role");
        } catch (JwtException e) {
            throw new IllegalStateException("Invalid token");
        }
    }
}