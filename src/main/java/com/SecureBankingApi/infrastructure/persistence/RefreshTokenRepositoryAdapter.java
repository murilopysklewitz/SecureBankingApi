package com.SecureBankingApi.infrastructure.persistence;

import com.SecureBankingApi.domain.RefreshToken;
import com.SecureBankingApi.domain.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Repository
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {
    private static final Logger log = LoggerFactory.getLogger(RefreshTokenRepositoryAdapter.class);
    private final SpringDataRefreshTokenRepository springData;
    private final RefreshTokenMapper mapper;

    public RefreshTokenRepositoryAdapter(SpringDataRefreshTokenRepository springData, RefreshTokenMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public void save(RefreshToken refreshToken) {
        RefreshTokenJpaEntity entity = mapper.toEntity(refreshToken);
        try {
            RefreshTokenJpaEntity saved = springData.save(entity);
        } catch (Exception e) {
            throw new RuntimeException("[REFRESH TOKEN REPOSITORY] Failed to save token "+ e);
        }
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        try {
            Optional<RefreshToken> entity = springData.findByToken(token).map((e) -> mapper.toDomain(e));
            return  entity;
        } catch (Exception e) {
            log.error("Failed to find with token: {}",token);
            throw new RuntimeException("[REFRESH TOKEN REPOSITORY] FAILED TO FIND BY TOKEN"+e);
        }
    }

    @Override
    @Transactional
    public void deleteByUserId(UUID userId) {
        try {
            springData.deleteByUserId(userId);
        }catch (Exception e){
            throw new RuntimeException("[REFRESH TOKEN REPOSITORY] Failed to delete user", e);
        }

    }

    @Override
    public void deleteExpiredToken() {
        try {
            springData.deleteExpiredTokens(LocalDateTime.now());
        } catch (Exception e) {
            throw new RuntimeException("[REFRESH TOKEN REPOSITORY] Failed to delete expired tokens"+e);
        }
    }
}
