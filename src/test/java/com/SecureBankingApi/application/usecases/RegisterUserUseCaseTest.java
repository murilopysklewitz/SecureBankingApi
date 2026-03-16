package com.SecureBankingApi.application.usecases;

import com.SecureBankingApi.application.exceptions.CpfAlreadyExistsException;
import com.SecureBankingApi.application.exceptions.EmailAlreadyExistsException;
import com.SecureBankingApi.application.usecases.createAccount.CreateAccountRequest;
import com.SecureBankingApi.application.usecases.createAccount.CreateAccountUseCase;
import com.SecureBankingApi.application.usecases.registerUser.RegisterUserRequest;
import com.SecureBankingApi.application.usecases.registerUser.RegisterUserUseCase;
import com.SecureBankingApi.domain.user.ports.PasswordHasher;
import com.SecureBankingApi.domain.user.ports.UserRepository;
import com.SecureBankingApi.domain.user.User;
import com.SecureBankingApi.domain.user.enums.UserRole;
import com.SecureBankingApi.domain.user.enums.UserStatus;
import com.SecureBankingApi.domain.user.valueObjects.CPF;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;

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

    private String hashPassword;


    @BeforeEach
    void SetUp() {
        request = new RegisterUserRequest(
                "Joao Bobao",
                "12345678901",
                "joaotestador@gmail.com",
                "securePassword"
        );

        hashPassword = "hashedPassword";
    }

    @Test
    void shouldCreateAnUser() throws Exception {
        when(hasher.hash(request.getPassword())).thenReturn(hashPassword);
        when(repository.existsByEmail(request.getEmail())).thenReturn(false);
        when(repository.existsByCpf(any(CPF.class))).thenReturn(false);

        // When
        var response = registerUserUseCase.execute(request);

        // Then
        assertNotNull(response);
        assertEquals(request.getFullName(), response.getFullName());
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(request.getCpf(), response.getCpf());
        assertEquals(UserRole.USER, response.getRole());
        assertEquals(UserStatus.ACTIVE, response.getStatus());
        assertNotNull(response.getCreatedAt());

        verify(hasher).hash(request.getPassword());
        verify(repository).existsByEmail(request.getEmail());
        verify(repository).existsByCpf(any(CPF.class));
        verify(repository).save(any(User.class));
        verify(createAccountUseCase).execute(any(CreateAccountRequest.class));
    }

    @Test
    void shouldThrowEmailAlreadyExistsExceptionWhenEmailExists() throws Exception {
        when(repository.existsByEmail(request.getEmail())).thenReturn(true);

        // When & Then
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class, () -> registerUserUseCase.execute(request));
        assertEquals(request.getEmail(), exception.getEmail());

        verify(repository).existsByEmail(request.getEmail());
        verify(repository, never()).existsByCpf(any(CPF.class));
        verify(repository, never()).save(any(User.class));
        verify(createAccountUseCase, never()).execute(any(CreateAccountRequest.class));
    }

    @Test
    void shouldThrowCpfAlreadyExistsExceptionWhenCpfExists() throws Exception {
        when(repository.existsByEmail(request.getEmail())).thenReturn(false);
        when(repository.existsByCpf(any(CPF.class))).thenReturn(true);

        // When & Then
        CpfAlreadyExistsException exception = assertThrows(CpfAlreadyExistsException.class, () -> registerUserUseCase.execute(request));
        assertEquals(request.getCpf(), exception.getCpf());

        verify(repository).existsByEmail(request.getEmail());
        verify(repository).existsByCpf(any(CPF.class));
        verify(repository, never()).save(any(User.class));
        verify(createAccountUseCase, never()).execute(any(CreateAccountRequest.class));
    }
}
