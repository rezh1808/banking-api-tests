package com.bank.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    private final String SECRET_STRING = "reza_electrical_engineer_banking_app_secret_key_123456";
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));

    public String generateToken(String username) {
        long EXPIRATION_TIME = 86400000;
        return Jwts.builder()
                .header().add("typ", "JWT").and() // Optional: Explicitly add header
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String validateTokenAndGetUsername(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}