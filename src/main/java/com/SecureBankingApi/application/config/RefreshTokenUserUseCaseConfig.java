package com.SecureBankingApi.application.config;


import com.SecureBankingApi.application.services.JwtService;
import com.SecureBankingApi.application.services.RefreshTokenService;
import com.SecureBankingApi.application.usecases.RefreshTokenUserUseCase;
import com.SecureBankingApi.domain.user.ports.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RefreshTokenUserUseCaseConfig {
    @Bean
    public RefreshTokenUserUseCase refreshTokenUserUseCase(RefreshTokenService refreshTokenService, JwtService jwtService, UserRepository userRepository) {
        return new RefreshTokenUserUseCase(refreshTokenService, jwtService, userRepository);
    }
}
