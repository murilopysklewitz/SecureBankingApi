package com.SecureBankingApi.domain.transaction;

import com.SecureBankingApi.domain.account.AccountNumber;
import com.SecureBankingApi.domain.account.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {
    private AccountDataTransaction source;
    private AccountDataTransaction destination;
    private Money amount;

    @BeforeEach
    void setUp() {
        source = AccountDataTransaction.of(
                UUID.randomUUID(),
                UUID.randomUUID(),
                AccountNumber.generate(),
                "001"
        );
        destination = AccountDataTransaction.of(
                UUID.randomUUID(),
                UUID.randomUUID(),
                AccountNumber.generate(),
                "001"
        );
        amount = Money.of(BigDecimal.valueOf(100.00));
    }

    @Test
    void shouldCreateTransferTransaction() {
        Transaction transaction = Transaction.create(
                source,
                destination,
                TransactionType.TRANSFER,
                amount,
                "Test transfer"
        );

        assertNotNull(transaction);
        assertNull(transaction.getId());
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
        assertEquals(TransactionType.TRANSFER, transaction.getType());
        assertEquals(amount.getValue(), transaction.getAmount().getValue());
        assertEquals("Test transfer", transaction.getDescription());
        assertNotNull(transaction.getCreatedAt());
        assertNull(transaction.getCompletedAt());
    }

    @Test
    void shouldThrowExceptionWhenSourceIsNull() {
        assertThrows(RuntimeException.class, () ->
                Transaction.create(null, destination, TransactionType.TRANSFER, amount, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenDestinationIsNull() {
        assertThrows(RuntimeException.class, () ->
                Transaction.create(source, null, TransactionType.TRANSFER, amount, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenTypeIsNull() {
        assertThrows(RuntimeException.class, () ->
                Transaction.create(source, destination, null, amount, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNull() {
        assertThrows(RuntimeException.class, () ->
                Transaction.create(source, destination, TransactionType.TRANSFER, null, null)
        );
    }

    @Test
    void shouldCompleteTransaction() {
        Transaction transaction = Transaction.create(
                source, destination, TransactionType.TRANSFER, amount, null
        );

        transaction.completeTransaction();

        assertEquals(TransactionStatus.COMPLETED, transaction.getStatus());
        assertNotNull(transaction.getCompletedAt());
    }

    @Test
    void shouldFailTransaction() {
        // Arrange
        Transaction transaction = Transaction.create(
                source, destination, TransactionType.TRANSFER, amount, null
        );

        transaction.failTransaction();

        assertEquals(TransactionStatus.FAILED, transaction.getStatus());
    }

    @Test
    void shouldReverseCompletedTransaction() {
        Transaction transaction = Transaction.create(
                source, destination, TransactionType.TRANSFER, amount, null
        );
        transaction.completeTransaction();

        transaction.ReverseTransaction();

        assertEquals(TransactionStatus.REVERSED, transaction.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenReversingNonCompletedTransaction() {
        Transaction transaction = Transaction.create(
                source, destination, TransactionType.TRANSFER, amount, null
        );

        assertThrows(RuntimeException.class, transaction::ReverseTransaction);
    }

    @Test
    void shouldThrowExceptionWhenCompletingAlreadyCompletedTransaction() {
        Transaction transaction = Transaction.create(
                source, destination, TransactionType.TRANSFER, amount, null
        );
        transaction.completeTransaction();

        assertThrows(RuntimeException.class, transaction::completeTransaction);
    }

    @Test
    void shouldCheckIfTransactionIsCompleted() {
        Transaction transaction = Transaction.create(
                source, destination, TransactionType.TRANSFER, amount, null
        );
        transaction.completeTransaction();
        assertTrue(transaction.isCompleted());
        assertFalse(transaction.isFailed());
        assertFalse(transaction.isReversed());
    }

    @Test
    void shouldCheckIfTransactionIsFailed() {
        Transaction transaction = Transaction.create(
                source, destination, TransactionType.TRANSFER, amount, null
        );

        transaction.failTransaction();

        assertTrue(transaction.isFailed());
        assertFalse(transaction.isCompleted());
        assertFalse(transaction.isReversed());
    }

    @Test
    void shouldCheckIfTransactionIsReversed() {
        Transaction transaction = Transaction.create(
                source, destination, TransactionType.TRANSFER, amount, null
        );
        transaction.completeTransaction();

        transaction.ReverseTransaction();

        assertTrue(transaction.isReversed());
        assertFalse(transaction.isFailed());
    }
}
