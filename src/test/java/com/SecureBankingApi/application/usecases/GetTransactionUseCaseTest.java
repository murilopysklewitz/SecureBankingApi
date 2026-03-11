package com.SecureBankingApi.application.usecases;

import com.SecureBankingApi.application.usecases.createTransaction.TransactionResponse;
import com.SecureBankingApi.application.usecases.getTransactionUseCase.GetTransactionUseCase;
import com.SecureBankingApi.domain.account.Money;
import com.SecureBankingApi.domain.transaction.*;
import com.SecureBankingApi.domain.transaction.exceptions.TransactionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
class GetTransactionUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    private GetTransactionUseCase useCase;

    private UUID transactionId;
    private UUID sourceUserId;
    private UUID destinationUserId;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        useCase = new GetTransactionUseCase(transactionRepository);

        transactionId = UUID.randomUUID();
        sourceUserId = UUID.randomUUID();
        destinationUserId = UUID.randomUUID();

        AccountDataTransaction source = AccountDataTransaction.of(
                sourceUserId,
                UUID.randomUUID(),
                "12345-6",
                "001"
        );

        AccountDataTransaction destination = AccountDataTransaction.of(
                destinationUserId,
                UUID.randomUUID(),
                "65432-1",
                "001"
        );

        transaction = Transaction.restore(
                transactionId,
                destination,
                source,
                TransactionStatus.COMPLETED,
                TransactionType.TRANSFER,
                "Test transfer",
                Money.of(BigDecimal.valueOf(100.00)),
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
        );
    }

    @Test
    void shouldGetTransactionAsSourceUser() {
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        TransactionResponse response = useCase.execute(transactionId, sourceUserId, false);

        assertNotNull(response);
        assertEquals(transactionId, response.getId());

        verify(transactionRepository, times(1)).findById(transactionId);
    }

    @Test
    void shouldGetTransactionAsDestinationUser() {
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        TransactionResponse response = useCase.execute(transactionId, destinationUserId, false);

        assertNotNull(response);
        assertEquals(transactionId, response.getId());
    }

    @Test
    void shouldGetTransactionAsAdmin() {
        UUID adminUserId = UUID.randomUUID();

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        TransactionResponse response = useCase.execute(transactionId, adminUserId, true);

        assertNotNull(response);
        assertEquals(transactionId, response.getId());
    }

    @Test
    void shouldThrowExceptionWhenTransactionNotFound() {
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class,
                () -> useCase.execute(transactionId, sourceUserId, false));
    }

    @Test
    void shouldThrowExceptionWhenUserHasNoPermission() {
        UUID unauthorizedUserId = UUID.randomUUID();

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(transactionId, unauthorizedUserId, false)
        );

        assertEquals("You don't have permission to view this transaction",
                exception.getMessage());
    }

    @Test
    void shouldHandleTransactionWithNullSource() {
        Transaction depositTransaction = Transaction.restore(
                transactionId,
                null,
                AccountDataTransaction.of(destinationUserId, UUID.randomUUID(), "12345-6", "001"),
                TransactionStatus.COMPLETED,
                TransactionType.DEPOSIT,
                "Deposit",
                Money.of(BigDecimal.valueOf(50.00)),
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
        );

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(depositTransaction));

        TransactionResponse response = useCase.execute(transactionId, destinationUserId, false);

        assertNotNull(response);
    }
}