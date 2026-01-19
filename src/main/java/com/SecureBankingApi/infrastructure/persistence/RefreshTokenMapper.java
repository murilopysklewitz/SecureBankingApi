package com.SecureBankingApi.infrastructure.persistence;

import com.SecureBankingApi.domain.RefreshToken;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenMapper {
    public RefreshToken toDomain(RefreshTokenJpaEntity entity){
        RefreshToken domain = RefreshToken.restore(
                entity.getId(),
                entity.getToken(),
                entity.getUserId(),
                entity.isRevoked(),
                entity.getCreatedAt(),
                entity.getExpiresAt()
        );
        return domain;
    }

    public RefreshTokenJpaEntity toEntity (RefreshToken domain){
        RefreshTokenJpaEntity entity =  new RefreshTokenJpaEntity(
                domain.getId(),
                domain.getUserId(),
                domain.getToken(),
                domain.isRevoked(),
                domain.getExpiresAt(),
                domain.getCreatedAt()
        );
        return entity;
    }
}
