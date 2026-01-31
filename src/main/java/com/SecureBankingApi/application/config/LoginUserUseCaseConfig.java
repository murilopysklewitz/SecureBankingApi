package com.SecureBankingApi.application.config;

import com.SecureBankingApi.application.services.JwtService;
import com.SecureBankingApi.application.services.RefreshTokenService;
import com.SecureBankingApi.application.usecases.LoginUserUseCase;
import com.SecureBankingApi.domain.user.ports.PasswordHasher;
import com.SecureBankingApi.domain.user.ports.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoginUserUseCaseConfig {
    @Bean
    public LoginUserUseCase loginUserUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            JwtService jwtService,
            RefreshTokenService refreshTokenService
    ) {
        return new LoginUserUseCase(userRepository, passwordHasher, jwtService, refreshTokenService);
    }
}
