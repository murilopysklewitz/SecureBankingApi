package com.SecureBankingApi.application.usecases;

import com.SecureBankingApi.application.usecases.createTransaction.TransactionRequest;
import com.SecureBankingApi.application.usecases.createTransaction.TransactionResponse;
import com.SecureBankingApi.application.usecases.createTransaction.TransferMoneyUseCase;
import com.SecureBankingApi.domain.account.*;
import com.SecureBankingApi.domain.transaction.AccountDataTransaction;
import com.SecureBankingApi.domain.transaction.TransactionRepository;
import com.SecureBankingApi.domain.transaction.TransactionStatus;
import com.SecureBankingApi.domain.transaction.TransactionType;
import com.SecureBankingApi.domain.transaction.exceptions.InvalidAccountData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferMoneyUseCaseTest {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountRepository accountRepository;

    private TransferMoneyUseCase useCase;

    private UUID sourceAccountId;
    private UUID destinationAccountId;

    private UUID userId;

    private Account sourceAccount;
    private Account destinationAccount;

    private AccountNumber accountNumber;
    private AccountNumber accountNumber2;

    @BeforeEach
    void SetUp() {
        useCase = new TransferMoneyUseCase(
                transactionRepository,
                accountRepository
        );
        sourceAccountId = UUID.randomUUID();

        destinationAccountId = UUID.randomUUID();

        userId = UUID.randomUUID();

        accountNumber = AccountNumber.generate();
        accountNumber2 = AccountNumber.generate();

        sourceAccount = Account.restore(
                sourceAccountId,
                userId,
                accountNumber.getValue(),
                "001",
                Money.of(BigDecimal.valueOf(100)),
                AccountStatus.ACTIVE,
                AccountType.CHECKING,
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
        );
        destinationAccount = Account.restore(
                destinationAccountId,
                UUID.randomUUID(),
                accountNumber2.getValue(),
                "001",
                Money.of(BigDecimal.valueOf(50.0)),
                AccountStatus.ACTIVE,
                AccountType.CHECKING,
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
        );
    }

    @Test
    void ShouldTransferMoneySuccessfully() {
        Money moneyToTransfer  =Money.of(BigDecimal.valueOf(50.0));
        TransactionRequest request = new TransactionRequest(
                sourceAccount.getId(),
                destinationAccount.getId(),
                TransactionType.TRANSFER,
                moneyToTransfer
        );

        when(accountRepository.findById(sourceAccount.getId())).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(destinationAccount.getId())).thenReturn(Optional.of(destinationAccount));

        TransactionResponse response = useCase.execute(request, userId);

        assertNotNull(response);
        assertEquals(TransactionStatus.COMPLETED, response.getStatus());
        assertEquals(BigDecimal.valueOf(50.00), response.getAmount());

        verify(accountRepository, times(1)).save(sourceAccount);
        verify(accountRepository, times(1)).save(destinationAccount);
        verify(transactionRepository, times(1)).save(any());

        assertEquals(BigDecimal.valueOf(50.00), sourceAccount.getBalance().getValue());
        assertEquals(BigDecimal.valueOf(100.00), destinationAccount.getBalance().getValue());
    }

    @Test
    void ShouldThrowExceptionWhenSourceAccountNotFound() {
        TransactionRequest request = new TransactionRequest(
                sourceAccountId,
                destinationAccountId,
                TransactionType.TRANSFER,
                Money.of(BigDecimal.valueOf(10))
        );

        when(accountRepository.findById(sourceAccountId)). thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> useCase.execute(request, userId));
    }
}
