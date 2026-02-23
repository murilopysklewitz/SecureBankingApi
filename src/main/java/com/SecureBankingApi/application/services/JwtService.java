package com.SecureBankingApi.application.services;

import com.SecureBankingApi.domain.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.xml.crypto.Data;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.Instant;
import java.util.UUID;

@Service
public class JwtService {
    private final SecretKey secretKey;
    private final long accessTokenExpiration;

    public JwtService(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration) {

        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
    }



    public String generateAccessToken(User user){

        Instant now = Instant.now();
        Instant expiration = now.plusMillis(accessTokenExpiration);

        return Jwts.builder()
                //  1. user id
                .subject(user.getId().toString())

                //  2. custom claims
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .claim("name", user.getFullName())
                //  3. Timestamps
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                //  secret key signature
                .signWith(secretKey)
                //'generate string
                .compact();
    }

    public boolean isValidToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;

        } catch (JwtException e) {
            return false;
        }
    }

    public UUID extractUserId(String token){
        Claims claims = extractClaims(token);
        return UUID.fromString(claims.getSubject());
    }

    public String extractEmail(String token) {
        Claims claims = extractClaims(token);
        return claims.get("email", String.class);
    }

    public String extractRole(String token) {
        Claims claims = extractClaims(token);
        return claims.get("role", String.class);
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getExpiration().before(new java.util.Date());
        } catch (Exception e) {
            return true;
        }
    }

}
