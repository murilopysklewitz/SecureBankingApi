package com.SecureBankingApi.application.services;

import com.SecureBankingApi.application.exceptions.InvalidRefreshToken;
import com.SecureBankingApi.domain.refreshToken.RefreshToken;
import com.SecureBankingApi.domain.refreshToken.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final long refreshTokenExpiration;

    public RefreshTokenService(RefreshTokenRepository repository, @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration ) {
        this.repository = repository;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public RefreshToken generateRefreshToken(UUID userId){
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000);

        RefreshToken refreshToken = RefreshToken.create(userId, expiresAt);

        repository.save(refreshToken);

        return refreshToken;
    }

    public RefreshToken validateRefreshToken(String tokenString){
        RefreshToken refreshToken = repository.findByToken(tokenString).orElseThrow(() -> new InvalidRefreshToken("Refresh Token not found"));

        if (!refreshToken.isValid()){
            throw new InvalidRefreshToken("Token invalid or expired");
        }

        return refreshToken;
    }

    public void revokeRefreshToken(String tokenString){
        RefreshToken refreshToken = repository.findByToken(tokenString)
                .orElseThrow(() -> new InvalidRefreshToken("Refresh Token not found"));

        refreshToken.revoke();
        repository.save(refreshToken);
    }

    public void deleteExpiredTokens() {
        repository.deleteExpiredToken();
    }
}
