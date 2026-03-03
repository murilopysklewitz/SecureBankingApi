package com.SecureBankingApi.infrastructure.persistence.transaction;

import com.SecureBankingApi.domain.account.Money;
import com.SecureBankingApi.domain.transaction.AccountDataTransaction;
import com.SecureBankingApi.domain.transaction.Transaction;
import com.SecureBankingApi.domain.transaction.TransactionStatus;
import com.SecureBankingApi.domain.transaction.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TransactionMapperTest {
    private TransactionMapper mapper;

    @BeforeEach
    void SetUp() {
        mapper = new TransactionMapper();
    }

    @Test
    void ShouldConvertEntityToDomain() {

        UUID sourceUserId = UUID.randomUUID();
        UUID sourceAccountId = UUID.randomUUID();
        String sourceAccountNumber = "12345-6";
        String sourceAgency = "001";

        AccountDataTransaction sourceData = AccountDataTransaction.of(
                sourceUserId,
                sourceAccountId,
                sourceAccountNumber,
                sourceAgency
        );

        UUID destinationUserId = UUID.randomUUID();
        UUID destinationAccountId = UUID.randomUUID();
        String destinationAccountNumber = "12345-6";
        String destinationAgency = "001";

        AccountDataTransaction destinationData = AccountDataTransaction.of(
                destinationUserId,
                destinationAccountId,
                destinationAccountNumber,
                destinationAgency
        );

        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime completed = now.plusMinutes(5);


        TransactionJpaEntity entity = new TransactionJpaEntity(
            id,
                sourceData.getUserId(),
                sourceAccountId,
                sourceData.getAccountNumber(),
                sourceData.getAgency(),
                destinationData.getUserId(),
                destinationData.getAccountId(),
                destinationData.getAccountNumber(),
                destinationData.getAgency(),
                TransactionStatus.PENDING,
                TransactionType.TRANSFER,
                "Test transaction",
                BigDecimal.valueOf(100.00),
                now,
                completed

        );

        Transaction domain = mapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals(id, domain.getId());
        assertEquals(sourceUserId, domain.getSource().getUserId());
        assertEquals("12345-6", domain.getSource().getAccountNumber());
        assertEquals(destinationUserId, domain.getReceiver().getUserId());
        assertEquals("12345-7", domain.getReceiver().getAccountNumber());
        assertEquals(TransactionStatus.PENDING, domain.getStatus());
        assertEquals(TransactionType.TRANSFER, domain.getType());
        assertEquals(BigDecimal.valueOf(100.00), domain.getAmount().getValue());
        assertEquals("Test transaction", domain.getDescription());
        assertEquals(now, domain.getCreatedAt());
        assertEquals(completed, domain.getCompletedAt());
    }

    @Test
    void shouldConvertDomainToEntitySuccessfully() {

        AccountDataTransaction source = AccountDataTransaction.of(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "12345-6",
                "001"
        );
        AccountDataTransaction destination = AccountDataTransaction.of(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "12345-7",
                "002"
        );
        Money amount = Money.of(BigDecimal.valueOf(100.00));

        Transaction domain = Transaction.create(
                destination,
                source,
                TransactionType.TRANSFER,
                amount,
                "Test transaction"
        );
        domain.completeTransaction();

        TransactionJpaEntity entity = mapper.toEntity(domain);

        assertNotNull(entity);
        assertEquals(domain.getId(), entity.getId());
        assertEquals(source.getUserId(), entity.getSourceUserId());
        assertEquals(source.getAccountNumber(), entity.getSourceAccountNumber());
        assertEquals(source.getAgency(), entity.getSourceAgency());
        assertEquals(destination.getUserId(), entity.getDestinationUserId());
        assertEquals(destination.getAccountNumber(), entity.getDestinationAccountNumber());
        assertEquals(destination.getAgency(), entity.getDestinationAgency());
        assertEquals(TransactionStatus.COMPLETED, entity.getStatus());
        assertEquals(TransactionType.TRANSFER, entity.getType());
        assertEquals(BigDecimal.valueOf(100.00), entity.getAmount());
        assertEquals("Test transaction", entity.getDescription());
    }
}
