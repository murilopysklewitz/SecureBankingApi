package com.SecureBankingApi.application.config;

import com.SecureBankingApi.application.usecases.createAccount.CreateAccountUseCase;
import com.SecureBankingApi.application.usecases.registerUser.RegisterUserUseCase;
import com.SecureBankingApi.domain.user.ports.PasswordHasher;
import com.SecureBankingApi.domain.user.ports.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RegisterUserUseCaseConfig {
    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepository repository,
            PasswordHasher passwordHasher,
            CreateAccountUseCase createAccountUseCase
    ){
        return new RegisterUserUseCase(passwordHasher, repository, createAccountUseCase);
    }
}
