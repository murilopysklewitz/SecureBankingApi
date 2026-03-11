package com.SecureBankingApi.application.usecases;

import com.SecureBankingApi.application.usecases.createTransaction.TransactionResponse;
import com.SecureBankingApi.application.usecases.getTransactionHistory.GetTransactionHistoryUseCase;
import com.SecureBankingApi.domain.account.*;
import com.SecureBankingApi.domain.account.exceptions.AccountNotFoundException;
import com.SecureBankingApi.domain.transaction.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetTransactionHistoryUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    private GetTransactionHistoryUseCase useCase;

    private UUID accountId;
    private UUID userId;
    private Account account;

    private UUID accountId2;
    private UUID userId2;
    private Account account2;

    @BeforeEach
    void setUp() {
        useCase = new GetTransactionHistoryUseCase(transactionRepository, accountRepository);

        accountId = UUID.randomUUID();
        userId = UUID.randomUUID();

        account = Account.restore(
                accountId,
                userId,
                "12345-6",
                "001",
                Money.of(BigDecimal.valueOf(1000.00)),
                AccountStatus.ACTIVE,
                AccountType.CHECKING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        accountId2 = UUID.randomUUID();
        userId2 = UUID.randomUUID();

        account2 = Account.restore(
                accountId2,
                userId2,
                "12345-7",
                "001",
                Money.of(BigDecimal.valueOf(50)),
                AccountStatus.ACTIVE,
                AccountType.CHECKING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void shouldGetTransactionHistorySuccessfully() {
        Transaction t1 = createTransaction(TransactionType.DEPOSIT, BigDecimal.valueOf(100.00));
        Transaction t2 = createTransaction(TransactionType.WITHDRAWAL, BigDecimal.valueOf(50.00));
        Transaction t3 = createTransaction(TransactionType.TRANSFER, BigDecimal.valueOf(200.00));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccountId(accountId)).thenReturn(List.of(t1, t2, t3));

        List<TransactionResponse> result = useCase.execute(accountId, userId, false);

        assertNotNull(result);
        assertEquals(3, result.size());

        verify(accountRepository, times(1)).findById(accountId);
        verify(transactionRepository, times(1)).findByAccountId(accountId);
    }

    @Test
    void shouldReturnEmptyListWhenNoTransactions() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccountId(accountId)).thenReturn(List.of());

        List<TransactionResponse> result = useCase.execute(accountId, userId, false);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFound() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> useCase.execute(accountId, userId, false));

        verify(transactionRepository, never()).findByAccountId(any());
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotAccountOwnerAndNotAdmin() {
        UUID otherUserId = UUID.randomUUID();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(accountId, otherUserId, false)
        );

        assertEquals("You don't have permission to view this account's transactions",
                exception.getMessage());
    }

    @Test
    void shouldAllowAdminToAccessAnyAccountHistory() {
        UUID adminUserId = UUID.randomUUID();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccountId(accountId)).thenReturn(List.of());

        List<TransactionResponse> result = useCase.execute(accountId, adminUserId, true);

        assertNotNull(result);
        verify(transactionRepository, times(1)).findByAccountId(accountId);
    }

    private Transaction createTransaction(TransactionType type, BigDecimal amount) {
        AccountDataTransaction sourceData = AccountDataTransaction.of(
                userId,
                accountId,
                "12345-6",
                "001"
        );
        AccountDataTransaction receiverData = AccountDataTransaction.of(
                userId,
                accountId,
                "12345-7",
                "001"
        );

        return Transaction.create(
                sourceData,
                receiverData,
                type,
                Money.of(amount),
                "Test transaction"
        );
    }
}