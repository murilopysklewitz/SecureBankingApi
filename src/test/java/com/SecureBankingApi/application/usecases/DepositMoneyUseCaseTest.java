package com.SecureBankingApi.application.usecases;

import com.SecureBankingApi.application.usecases.createTransaction.TransactionResponse;
import com.SecureBankingApi.application.usecases.depositMoney.DepositMoneyRequest;
import com.SecureBankingApi.application.usecases.depositMoney.DepositMoneyUseCase;
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
@DisplayName("Deposit Money Use Case Tests")
class DepositMoneyUseCaseTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private DepositMoneyUseCase useCase;

    private UUID accountId;
    private UUID userId;
    private Account account;
    private DepositMoneyRequest request;

    @BeforeEach
    void setUp() {
        useCase = new DepositMoneyUseCase(accountRepository, transactionRepository);

        accountId = UUID.randomUUID();
        userId = UUID.randomUUID();

        account = Account.restore(
                accountId,
                userId,
                "123456-6",
                "001",
                Money.of(BigDecimal.valueOf(100.00)),
                AccountStatus.ACTIVE,
                AccountType.CHECKING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        request = new DepositMoneyRequest(
                accountId,
                BigDecimal.valueOf(50.00),
                "Test deposit"
        );
    }

    @Test
    void shouldDepositMoneySuccessfully() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        BigDecimal initialBalance = account.getBalance().getValue();

        TransactionResponse response = useCase.execute(request, userId);

        assertNotNull(response);
        assertEquals(TransactionStatus.COMPLETED, response.getStatus());
        assertEquals(TransactionType.DEPOSIT, response.getType());
        assertEquals(BigDecimal.valueOf(50.00), response.getAmount());
        assertEquals("Test deposit", response.getDescription());

        BigDecimal expectedBalance = initialBalance.add(BigDecimal.valueOf(50.00));
        assertEquals(expectedBalance, account.getBalance().getValue());

        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(1)).save(account);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void shouldDepositWithoutDescription() {
        DepositMoneyRequest requestNoDescription = new DepositMoneyRequest(
                accountId,
                BigDecimal.valueOf(100.00),
                null
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        TransactionResponse response = useCase.execute(requestNoDescription, userId);

        assertNotNull(response);
        assertEquals(TransactionStatus.COMPLETED, response.getStatus());
        assertNull(response.getDescription());
        assertEquals(BigDecimal.valueOf(200.00), account.getBalance().getValue());
    }

    @Test
    void shouldDepositDecimalValue() {
        DepositMoneyRequest decimalRequest = new DepositMoneyRequest(
                accountId,
                new BigDecimal("123.45"),
                "Decimal deposit"
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        TransactionResponse response = useCase.execute(decimalRequest, userId);

        assertNotNull(response);
        assertEquals(new BigDecimal("123.45"), response.getAmount());
        assertEquals(new BigDecimal("223.45"), account.getBalance().getValue());
    }

    @Test
    @DisplayName("Should throw exception when account not found")
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

        assertEquals("You can only deposit to your own accounts", exception.getMessage());
        assertEquals(BigDecimal.valueOf(100.00), account.getBalance().getValue());

        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenAccountIsBlocked() {
        account.block();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(request, userId)
        );

        assertEquals("Account is blocked", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenAccountIsClosed() {
        Account closedAccount = Account.restore(
                accountId,
                userId,
                "123456-6",
                "001",
                Money.zero(),
                AccountStatus.ACTIVE,
                AccountType.CHECKING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        closedAccount.close();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(closedAccount));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(request, userId)
        );

        assertEquals("Account is inative", exception.getMessage());
    }

    @Test
    void shouldCallMethodsInCorrectOrder() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        var inOrder = inOrder(accountRepository, transactionRepository);

        useCase.execute(request, userId);

        inOrder.verify(accountRepository).findById(accountId);
        inOrder.verify(accountRepository).save(account);
        inOrder.verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void shouldUpdateBalanceIncrementallyInMultipleDeposits() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        useCase.execute(new DepositMoneyRequest(accountId, BigDecimal.valueOf(10.00), "D1"), userId);
        useCase.execute(new DepositMoneyRequest(accountId, BigDecimal.valueOf(20.00), "D2"), userId);
        useCase.execute(new DepositMoneyRequest(accountId, BigDecimal.valueOf(30.00), "D3"), userId);

        assertEquals(BigDecimal.valueOf(160.00), account.getBalance().getValue());

        verify(accountRepository, times(3)).save(account);
        verify(transactionRepository, times(3)).save(any(Transaction.class));
    }
}