package com.SecureBankingApi.application.config;

import com.SecureBankingApi.application.services.RefreshTokenService;
import com.SecureBankingApi.application.usecases.logoutUser.LogoutUserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogoutUserUseCaseConfig {
    @Bean
    public LogoutUserUseCase logoutUserUseCase(
            RefreshTokenService refreshTokenService
    ){
        return new LogoutUserUseCase(refreshTokenService);
    }
}
