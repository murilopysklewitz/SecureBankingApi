package com.SecureBankingApi.application.config;

import com.SecureBankingApi.application.services.RefreshTokenService;
import com.SecureBankingApi.domain.refreshToken.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RefreshTokenServiceConfig {

    @Bean
    public RefreshTokenService refreshTokenService(
            RefreshTokenRepository repository,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ){
        return new RefreshTokenService(repository, refreshTokenExpiration);
    }
}
