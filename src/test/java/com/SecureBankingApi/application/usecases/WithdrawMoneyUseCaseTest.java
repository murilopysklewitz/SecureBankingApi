package com.SecureBankingApi.application.usecases;

import com.SecureBankingApi.application.usecases.createTransaction.TransactionResponse;
import com.SecureBankingApi.application.usecases.withdrawMoney.WithdrawMoneyRequest;
import com.SecureBankingApi.application.usecases.withdrawMoney.WithdrawMoneyUseCase;
import com.SecureBankingApi.domain.account.*;
import com.SecureBankingApi.domain.account.exceptions.AccountNotFoundException;
import com.SecureBankingApi.domain.transaction.Transaction;
import com.SecureBankingApi.domain.transaction.TransactionRepository;
import com.SecureBankingApi.domain.transaction.TransactionStatus;
import com.SecureBankingApi.domain.transaction.TransactionType;
import com.SecureBankingApi.domain.transaction.exceptions.InvalidTransactionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WithdrawMoneyUseCaseTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private WithdrawMoneyUseCase useCase;

    private UUID accountId;
    private UUID userId;
    private Account account;
    private WithdrawMoneyRequest request;

    @BeforeEach
    void setUp() {
        useCase = new WithdrawMoneyUseCase(accountRepository, transactionRepository);

        accountId = UUID.randomUUID();
        userId = UUID.randomUUID();

        account = Account.restore(
                accountId,
                userId,
                "12345-6",
                "001",
                Money.of(BigDecimal.valueOf(500.00)),
                AccountStatus.ACTIVE,
                AccountType.CHECKING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        request = new WithdrawMoneyRequest(
                accountId,
                BigDecimal.valueOf(100.00),
                "Test withdrawal"
        );
    }

    @Test
    void shouldWithdrawMoneySuccessfully() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        BigDecimal initialBalance = account.getBalance().getValue();

        TransactionResponse response = useCase.execute(request, userId);

        assertNotNull(response);
        assertEquals(TransactionStatus.COMPLETED, response.getStatus());
        assertEquals(TransactionType.WITHDRAWAL, response.getType());
        assertEquals(BigDecimal.valueOf(100.00), response.getAmount());

        BigDecimal expectedBalance = initialBalance.subtract(BigDecimal.valueOf(100.00));
        assertEquals(expectedBalance, account.getBalance().getValue());

        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(1)).save(account);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void shouldWithdrawEntireBalance() {
        WithdrawMoneyRequest withdrawAll = new WithdrawMoneyRequest(
                accountId,
                BigDecimal.valueOf(500.00),
                "Withdraw all"
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        TransactionResponse response = useCase.execute(withdrawAll, userId);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(0.0), account.getBalance().getValue());
    }

    @Test
    void shouldWithdrawDecimalValue() {
        WithdrawMoneyRequest decimalRequest = new WithdrawMoneyRequest(
                accountId,
                new BigDecimal("123.45"),
                "Decimal withdrawal"
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        TransactionResponse response = useCase.execute(decimalRequest, userId);

        assertNotNull(response);
        assertEquals(new BigDecimal("123.45"), response.getAmount());
        assertEquals(new BigDecimal("376.55"), account.getBalance().getValue());
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFound() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> useCase.execute(request, userId));

        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotAccountOwner() {
        UUID otherUserId = UUID.randomUUID();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        InvalidTransactionException exception = assertThrows(
                InvalidTransactionException.class,
                () -> useCase.execute(request, otherUserId)
        );

        assertEquals("You can only withdraw from your own accounts", exception.getMessage());
        assertEquals(BigDecimal.valueOf(500.00), account.getBalance().getValue());
    }

    @Test
    void shouldThrowExceptionWhenInsufficientBalance() {
        WithdrawMoneyRequest excessiveWithdraw = new WithdrawMoneyRequest(
                accountId,
                BigDecimal.valueOf(600.00),
                "Excessive withdrawal"
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        InvalidTransactionException exception = assertThrows(
                InvalidTransactionException.class,
                () -> useCase.execute(excessiveWithdraw, userId)
        );

        assertEquals("insufficient balance", exception.getMessage());
        assertEquals(BigDecimal.valueOf(500.00), account.getBalance().getValue());

        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenAccountIsBlocked() {
        account.block();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(request, userId));
    }

    @Test
    void shouldCreateWithdrawalTransactionWithCorrectData() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        useCase.execute(request, userId);

        verify(transactionRepository).save(captor.capture());
        Transaction savedTransaction = captor.getValue();

        assertEquals(TransactionType.WITHDRAWAL, savedTransaction.getType());
        assertEquals(TransactionStatus.COMPLETED, savedTransaction.getStatus());

        assertNotNull(savedTransaction.getReceiver());
        assertEquals(userId, savedTransaction.getReceiver().getUserId());
        assertEquals(accountId, savedTransaction.getReceiver().getAccountId());

        assertNull(savedTransaction.getSource());
    }

    @Test
    void shouldValidateBalanceBeforeDebiting() {
        Account lowBalanceAccount = Account.restore(
                accountId,
                userId,
                "12345-6",
                "001",
                Money.of(BigDecimal.valueOf(50.00)),
                AccountStatus.ACTIVE,
                AccountType.CHECKING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        WithdrawMoneyRequest largeRequest = new WithdrawMoneyRequest(
                accountId,
                BigDecimal.valueOf(100.00),
                "Large withdrawal"
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(lowBalanceAccount));

        assertThrows(InvalidTransactionException.class, () -> useCase.execute(largeRequest, userId));

        assertEquals(BigDecimal.valueOf(50.00), lowBalanceAccount.getBalance().getValue());
    }
}