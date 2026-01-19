package com.SecureBankingApi.domain;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {
    void save(RefreshToken refreshToken);
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserId(UUID userId);
    void deleteExpiredToken();
}
