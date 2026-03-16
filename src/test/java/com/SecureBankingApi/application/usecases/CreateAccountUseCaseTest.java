package com.SecureBankingApi.application.usecases;

import com.SecureBankingApi.application.exceptions.BusinessException;
import com.SecureBankingApi.application.usecases.createAccount.CreateAccountRequest;
import com.SecureBankingApi.application.usecases.createAccount.CreateAccountUseCase;
import com.SecureBankingApi.domain.account.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateAccountUseCaseTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private CreateAccountUseCase createAccountUseCase;

    private UUID userId;
    private AccountType accountType;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        accountType = AccountType.CHECKING;
    }

    @Test
    public void shouldCreateAccountSuccessfully() {
        // Given
        CreateAccountRequest request = new CreateAccountRequest(userId, accountType);
        when(accountRepository.existsByUserIdAndType(userId, accountType)).thenReturn(false);

        // When
        var response = createAccountUseCase.execute(request);

        // Then
        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals(accountType, response.getType());
        assertEquals("001", response.getAgency());
        assertEquals(AccountStatus.ACTIVE, response.getStatus());
        assertEquals(Money.zero(), response.getBalance());
        assertNotNull(response.getAccountNumber());
        assertNull(response.getId());
        assertNotNull(response.getCreatedAt());

        verify(accountRepository).existsByUserIdAndType(userId, accountType);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    public void shouldThrowBusinessExceptionWhenAccountOfSameTypeAlreadyExists() {
        // Given
        CreateAccountRequest request = new CreateAccountRequest(userId, accountType);
        when(accountRepository.existsByUserIdAndType(userId, accountType)).thenReturn(true);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> createAccountUseCase.execute(request));
        assertEquals("Already have an account with same time for this user", exception.getMessage());

        verify(accountRepository).existsByUserIdAndType(userId, accountType);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void shouldGenerateUniqueAccountNumber() {
        // Given
        CreateAccountRequest request1 = new CreateAccountRequest(userId, accountType);
        CreateAccountRequest request2 = new CreateAccountRequest(UUID.randomUUID(), accountType);
        when(accountRepository.existsByUserIdAndType(any(UUID.class), eq(accountType))).thenReturn(false);

        // When
        var response1 = createAccountUseCase.execute(request1);
        var response2 = createAccountUseCase.execute(request2);

        // Then
        assertNotEquals(response1.getAccountNumber(), response2.getAccountNumber());
    }
}
