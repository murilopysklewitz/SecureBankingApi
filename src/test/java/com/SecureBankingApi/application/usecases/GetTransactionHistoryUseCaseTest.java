package com.SecureBankingApi.application.usecases;

import com.SecureBankingApi.application.usecases.createTransaction.TransactionResponse;
import com.SecureBankingApi.application.usecases.getTransactionHistory.GetTransactionHistoryUseCase;
import com.SecureBankingApi.domain.PageRequest;
import com.SecureBankingApi.domain.PageResult;
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
@DisplayName("GetTransactionHistoryUseCase Tests")
class GetTransactionHistoryUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    private GetTransactionHistoryUseCase useCase;

    private UUID accountId;
    private UUID userId;
    private Account account;
    private AccountNumber accountNumber;

    private UUID accountId2;
    private UUID userId2;
    private AccountNumber accountNumber2;
    private Account account2;

    @BeforeEach
    void setUp() {
        useCase = new GetTransactionHistoryUseCase(transactionRepository, accountRepository);

        accountId = UUID.randomUUID();
        userId = UUID.randomUUID();
        accountNumber = AccountNumber.generate();
        accountNumber2 = AccountNumber.generate();

        account = Account.restore(
                accountId,
                userId,
                accountNumber.getValue(),
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
                accountNumber2.getValue(),
                "001",
                Money.of(BigDecimal.valueOf(50)),
                AccountStatus.ACTIVE,
                AccountType.CHECKING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("should get transaction history successfully with pagination")
    void shouldGetTransactionHistorySuccessfully() {
        Transaction t1 = createTransaction(TransactionType.DEPOSIT, BigDecimal.valueOf(100.00));
        Transaction t2 = createTransaction(TransactionType.WITHDRAWAL, BigDecimal.valueOf(50.00));
        Transaction t3 = createTransaction(TransactionType.TRANSFER, BigDecimal.valueOf(200.00));

        PageRequest pageRequest = new PageRequest("createdAt", "DESC", 10, 0);
        PageResult<Transaction> pageResult = new PageResult<>(
                0,
                10,
                3L,
                List.of(t1, t2, t3)
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccountIdPage(accountId, pageRequest)).thenReturn(pageResult);

        PageResult<TransactionResponse> result = useCase.execute(accountId, userId, false, pageRequest);

        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals(0, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(3L, result.getTotalElements());

        verify(accountRepository, times(1)).findById(accountId);
        verify(transactionRepository, times(1)).findByAccountIdPage(accountId, pageRequest);
    }

    @Test
    @DisplayName("should return empty page when no transactions")
    void shouldReturnEmptyPageWhenNoTransactions() {
        PageRequest pageRequest = new PageRequest("createdAt", "DESC", 10, 0);
        PageResult<Transaction> emptyPageResult = new PageResult<>(
                0,
                10,
                0L,
                List.of()
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccountIdPage(accountId, pageRequest)).thenReturn(emptyPageResult);

        PageResult<TransactionResponse> result = useCase.execute(accountId, userId, false, pageRequest);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0L, result.getTotalElements());
    }

    @Test
    @DisplayName("should handle pagination correctly with multiple pages")
    void shouldHandlePaginationCorrectlyWithMultiplePages() {
        Transaction t1 = createTransaction(TransactionType.DEPOSIT, BigDecimal.valueOf(100.00));
        Transaction t2 = createTransaction(TransactionType.WITHDRAWAL, BigDecimal.valueOf(50.00));

        PageRequest pageRequest = new PageRequest("createdAt", "DESC", 2, 1);
        PageResult<Transaction> pageResult = new PageResult<>(
                1,
                2,
                5L,
                List.of(t1, t2)
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccountIdPage(accountId, pageRequest)).thenReturn(pageResult);

        PageResult<TransactionResponse> result = useCase.execute(accountId, userId, false, pageRequest);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(1, result.getPage());
        assertEquals(2, result.getSize());
        assertEquals(5L, result.getTotalElements());
    }

    @Test
    @DisplayName("should throw exception when account not found")
    void shouldThrowExceptionWhenAccountNotFound() {
        PageRequest pageRequest = new PageRequest("createdAt", "DESC", 10, 0);

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> useCase.execute(accountId, userId, false, pageRequest));

        verify(transactionRepository, never()).findByAccountIdPage(any(), any());
    }

    @Test
    @DisplayName("should throw exception when user is not account owner and not admin")
    void shouldThrowExceptionWhenUserIsNotAccountOwnerAndNotAdmin() {
        UUID otherUserId = UUID.randomUUID();
        PageRequest pageRequest = new PageRequest("createdAt", "DESC", 10, 0);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(accountId, otherUserId, false, pageRequest)
        );

        assertEquals("You don't have permission to view this account's transactions",
                exception.getMessage());
    }

    @Test
    @DisplayName("should allow admin to access any account history")
    void shouldAllowAdminToAccessAnyAccountHistory() {
        UUID adminUserId = UUID.randomUUID();
        PageRequest pageRequest = new PageRequest("createdAt", "DESC", 10, 0);

        PageResult<Transaction> emptyPageResult = new PageResult<>(
                0,
                10,
                0L,
                List.of()
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccountIdPage(accountId, pageRequest)).thenReturn(emptyPageResult);

        PageResult<TransactionResponse> result = useCase.execute(accountId, adminUserId, true, pageRequest);

        assertNotNull(result);
        verify(transactionRepository, times(1)).findByAccountIdPage(accountId, pageRequest);
    }

    @Test
    @DisplayName("should validate pagination parameters")
    void shouldValidatePaginationParameters() {
        PageRequest pageRequest = new PageRequest("createdAt", "DESC", 50, 0);
        Transaction t1 = createTransaction(TransactionType.DEPOSIT, BigDecimal.valueOf(100.00));

        PageResult<Transaction> pageResult = new PageResult<>(
                0,
                50,
                1L,
                List.of(t1)
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccountIdPage(accountId, pageRequest)).thenReturn(pageResult);

        PageResult<TransactionResponse> result = useCase.execute(accountId, userId, false, pageRequest);

        assertNotNull(result);
        assertEquals(50, result.getSize());
        assertEquals(0, result.getPage());
    }

    private Transaction createTransaction(TransactionType type, BigDecimal amount) {
        AccountDataTransaction sourceData = AccountDataTransaction.of(
                userId,
                accountId,
                accountNumber,
                "001"
        );
        AccountDataTransaction receiverData = AccountDataTransaction.of(
                userId,
                accountId,
                accountNumber2,
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