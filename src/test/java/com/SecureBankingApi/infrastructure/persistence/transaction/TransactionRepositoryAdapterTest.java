package com.SecureBankingApi.infrastructure.persistence.transaction;

import com.SecureBankingApi.domain.account.AccountNumber;
import com.SecureBankingApi.domain.account.Money;
import com.SecureBankingApi.domain.transaction.AccountDataTransaction;
import com.SecureBankingApi.domain.transaction.Transaction;
import com.SecureBankingApi.domain.transaction.TransactionStatus;
import com.SecureBankingApi.domain.transaction.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
public class TransactionRepositoryAdapterTest {

    private Transaction domain;
    private TransactionJpaEntity entity;

    @Mock
    private SpringDataTransactionRepository repository;
    @Mock
    private TransactionMapper mapper;

    private UUID id;

    private UUID receiverUserId;
    private UUID receiverAccountId;
    private AccountNumber receiverAccountNumber;
    private UUID accountId;

    private UUID sourceId;
    private UUID sourceUserId;
    private UUID sourceAccountId;
    private AccountNumber sourceAccountNumber;

    @InjectMocks
    private TransactionRepositoryAdapter adapter;


    @BeforeEach
    void SetUp() {
        id = UUID.randomUUID();

        receiverUserId = UUID.randomUUID();
        accountId = UUID.randomUUID();
        receiverAccountId = UUID.randomUUID();
        receiverAccountNumber = AccountNumber.generate();

        sourceId = UUID.randomUUID();
        sourceUserId = UUID.randomUUID();
        sourceAccountId = UUID.randomUUID();
        sourceAccountNumber = AccountNumber.generate();

        domain = Transaction.restore(
                id,
                AccountDataTransaction.of(
                        receiverUserId,
                        receiverAccountId,
                        receiverAccountNumber,
                        "001"
                ),
                AccountDataTransaction.of(
                        sourceUserId,
                        sourceAccountId,
                        sourceAccountNumber,
                        "001"
                ),
                TransactionStatus.PENDING,
                TransactionType.TRANSFER,
                "Transfer money",
                Money.of(BigDecimal.valueOf(10)),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10)
        );

        entity = new TransactionJpaEntity(
                id,

                sourceUserId,
                sourceAccountId,
                AccountNumber.generate().getValue(),
                "001",

                receiverUserId,
                receiverAccountId,
                AccountNumber.generate().getValue(),
                "001",

                TransactionStatus.PENDING,
                TransactionType.TRANSFER,
                "Transfer money",
                BigDecimal.valueOf(10),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10)
        );
    }
    @Test
    void ShouldSaveTransactionSuccessfully() {
        when(mapper.toEntity(domain)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(null);

        adapter.save(domain);

        verify(mapper, times(1)).toEntity(domain);
        verify(repository, times(1)).save(entity);
    }

    @Test
    void ShouldFindByIdSuccessfully() {
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);
        Optional<Transaction> result =adapter.findById(id);

        assertTrue(result.isPresent());
        assertEquals(domain, result.get());

        verify(mapper, times(1)).toDomain(entity);
        verify(repository, times(1)).findById(id);
    }
    @Test
    void ShouldReturnEmptyWhenFindById() {
        when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Transaction> result = adapter.findById(id);

        assertFalse(result.isPresent());
        verify(repository, times(1)).findById(id);
        verify(mapper, never()).toDomain(entity);
    }

    @Test
    void ShouldFindBySourceSuccessfully() {
        TransactionJpaEntity entity2 = new TransactionJpaEntity(
                UUID.randomUUID(),

                UUID.randomUUID(),
                UUID.randomUUID(),
                sourceAccountNumber.getValue(),
                "001",

                UUID.randomUUID(),
                UUID.randomUUID(),
                receiverAccountNumber.getValue(),
                "001",

                TransactionStatus.PENDING,
                TransactionType.TRANSFER,

                "test transaction",

                BigDecimal.valueOf(12),

                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10)
        );
        Transaction domain2 = Transaction.restore(
                entity2.getId(),

                AccountDataTransaction.of(
                        entity2.getDestinationUserId(),
                        entity2.getDestinationAccountId(),
                        AccountNumber.restore(entity2.getDestinationAccountNumber()),
                        entity2.getDestinationAgency()
                ),
                AccountDataTransaction.of(
                        entity2.getSourceUserId(),
                        entity2.getSourceAccountId(),
                        AccountNumber.restore(entity2.getSourceAccountNumber()),
                        entity2.getDestinationAgency()
                ),

                entity2.getStatus(),
                entity2.getType(),
                entity2.getDescription(),
                Money.of(entity2.getAmount()),
                entity2.getCreatedAt(),
                entity2.getCompletedAt()
        );
        List<TransactionJpaEntity> entities = List.of(entity, entity2);

        when(repository.findBySourceUserId(sourceUserId)).thenReturn(entities);
        when(mapper.toDomainList(entities)).thenReturn(List.of(domain, domain2));

        List<Transaction> result = adapter.findBySourceUserId(sourceUserId);

        assertEquals(domain, result.getFirst());
        assertEquals(domain2, result.get(1));
        assertEquals(2, result.size());

        verify(repository, times(1)).findBySourceUserId(sourceUserId);
        verify(mapper, times(1)).toDomainList(entities);
    }

    @Test
    void ShouldFindByAccountIdSuccessfully() {
        TransactionJpaEntity entity2 = new TransactionJpaEntity(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                sourceAccountNumber.getValue(),
                "001",
                UUID.randomUUID(),
                UUID.randomUUID(),
                receiverAccountNumber.getValue(),
                "001",
                TransactionStatus.PENDING,
                TransactionType.TRANSFER,
                "test transaction",
                BigDecimal.valueOf(12),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10)
        );
        Transaction domain2 = Transaction.restore(
                entity2.getId(),

                AccountDataTransaction.of(
                        entity2.getDestinationUserId(),
                        entity2.getDestinationAccountId(),
                        AccountNumber.restore(entity2.getDestinationAccountNumber()),
                        entity2.getDestinationAgency()
                ),
                AccountDataTransaction.of(
                        entity2.getSourceUserId(),
                        entity2.getSourceAccountId(),
                       AccountNumber.restore(entity2.getSourceAccountNumber()),
                        entity2.getSourceAgency()
                ),

                entity2.getStatus(),
                entity2.getType(),
                entity2.getDescription(),
                Money.of(entity2.getAmount()),
                entity2.getCreatedAt(),
                entity2.getCompletedAt()
        );
        List<TransactionJpaEntity> entities = List.of(entity, entity2);

        when(repository.findByAccountId(accountId)).thenReturn(entities);
        when(mapper.toDomainList(entities)).thenReturn(List.of(domain, domain2));

        List<Transaction> result = adapter.findByAccountId(accountId);

        assertEquals(domain, result.getFirst());
        assertEquals(domain2, result.get(1));
        assertEquals(2, result.size());
    }

    @Test
    void ShouldFindByStatus() {
        TransactionJpaEntity entity2 = new TransactionJpaEntity(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                AccountNumber.generate().getValue(),
                "001",
                UUID.randomUUID(),
                UUID.randomUUID(),
                AccountNumber.generate().getValue(),
                "001",
                TransactionStatus.PENDING,
                TransactionType.TRANSFER,
                "test transaction",
                BigDecimal.valueOf(12),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10)
        );
        Transaction domain2 = Transaction.restore(
                entity2.getId(),

                AccountDataTransaction.of(
                        entity2.getDestinationUserId(),
                        entity2.getDestinationAccountId(),
                        AccountNumber.restore(entity2.getDestinationAccountNumber()),
                        entity2.getDestinationAgency()
                ),
                AccountDataTransaction.of(
                        entity2.getSourceUserId(),
                        entity2.getSourceAccountId(),
                        AccountNumber.restore(entity2.getSourceAccountNumber()),
                        entity2.getSourceAgency()
                ),

                entity2.getStatus(),
                entity2.getType(),
                entity2.getDescription(),
                Money.of(entity2.getAmount()),
                entity2.getCreatedAt(),
                entity2.getCompletedAt()
        );
        List<TransactionJpaEntity> entities = List.of(entity, entity2);

        when(repository.findByStatus(TransactionStatus.PENDING)).thenReturn(entities);
        when(mapper.toDomainList(entities)).thenReturn(List.of(domain, domain2));

        List<Transaction> result = adapter.findByStatus(TransactionStatus.PENDING);

        assertEquals(domain, result.get(0));
        assertEquals(domain2, result.get(1));
        assertEquals(2, result.size());

        verify(repository, times(1)).findByStatus(TransactionStatus.PENDING);
        verify(mapper, times(1)).toDomainList(entities);
    }

    @Test
    void ShouldFindByPeriodSuccessfully() {
        TransactionJpaEntity entity2 = new TransactionJpaEntity(
                UUID.randomUUID(),
                UUID.randomUUID(),
                sourceAccountId,
                sourceAccountNumber.getValue(),
                "001",
                UUID.randomUUID(),
                receiverAccountId,
                receiverAccountNumber.getValue(),
                "001",
                TransactionStatus.PENDING,
                TransactionType.TRANSFER,
                "test transaction",
                BigDecimal.valueOf(12),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10)
        );
        Transaction domain2 = Transaction.restore(
                entity2.getId(),

                AccountDataTransaction.of(
                        entity2.getDestinationUserId(),
                        entity2.getDestinationAccountId(),
                        AccountNumber.restore(entity2.getDestinationAccountNumber()),
                        entity2.getDestinationAgency()
                ),
                AccountDataTransaction.of(
                        entity2.getSourceUserId(),
                        entity2.getSourceAccountId(),
                        AccountNumber.restore(entity2.getSourceAccountNumber()),
                        entity2.getSourceAgency()
                ),

                entity2.getStatus(),
                entity2.getType(),
                entity2.getDescription(),
                Money.of(entity2.getAmount()),
                entity2.getCreatedAt(),
                entity2.getCompletedAt()
        );
        List<TransactionJpaEntity> entities = List.of(entity, entity2);

        LocalDateTime start =LocalDateTime.now().minusMinutes(5);
        LocalDateTime end = LocalDateTime.now();

        when(repository.findByPeriod(sourceAccountId, start, end)).thenReturn(entities);
        when(mapper.toDomainList(entities)).thenReturn(List.of(domain, domain2));
        List<Transaction> result = adapter.findByPeriod(sourceAccountId, start, end);

        assertEquals(domain, result.get(0));
        assertEquals(domain2, result.get(1));
        assertEquals(2, result.size());

        verify(repository, times(1)).findByPeriod(sourceAccountId, start, end);
        verify(mapper, times(1)).toDomainList(entities);
    }

}
