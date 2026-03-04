package com.SecureBankingApi.application.usecases;

import com.SecureBankingApi.application.usecases.createAccount.CreateAccountUseCase;
import com.SecureBankingApi.application.usecases.registerUser.RegisterUserRequest;
import com.SecureBankingApi.application.usecases.registerUser.RegisterUserUseCase;
import com.SecureBankingApi.domain.user.ports.PasswordHasher;
import com.SecureBankingApi.domain.user.ports.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RegisterUserUseCaseTest {
    @Mock
    private PasswordHasher hasher;

    @Mock
    private UserRepository repository;

    @Mock
    private CreateAccountUseCase createAccountUseCase;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    private RegisterUserRequest request;

    @BeforeEach
    void SetUp() {
        request = new RegisterUserRequest(
                "Joao Bobao",
                "12345678901",
                "joaotestador@gmail.com",
                "securePassword"
        );

    }
}
