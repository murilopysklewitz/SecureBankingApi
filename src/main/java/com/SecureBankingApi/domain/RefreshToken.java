package com.SecureBankingApi.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class RefreshToken {
    private UUID id;
    private String token;
    private UUID userId;

    private boolean revoked;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    private RefreshToken(String token, UUID userId, LocalDateTime expiresAt) {
        this.token = token;
        this.userId = userId;
        this.expiresAt = expiresAt;

        this.createdAt = LocalDateTime.now();
        this.id = null;
        this.revoked = false;
    }

    public static RefreshToken create(UUID userId, LocalDateTime expiresAt){
        String token = UUID.randomUUID().toString();
        return new RefreshToken(token, userId, expiresAt);
    }

    public static RefreshToken restore(UUID id, String token, UUID userId, boolean revoked, LocalDateTime createdAt, LocalDateTime expiresAt) {
        RefreshToken refreshToken = new RefreshToken(
                token,
                userId,
                expiresAt
        );
        refreshToken.id = id;
        refreshToken.revoked = revoked;
        refreshToken.createdAt = createdAt;

        return refreshToken;
    }

    public boolean isValid() {
        return !revoked&& LocalDateTime.now() .isBefore(expiresAt);
    }

    public  void revoke(){
        this.revoked = true;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public UUID getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public UUID getUserId() {
        return userId;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
}
