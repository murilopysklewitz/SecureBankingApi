package com.SecureBankingApi.infrastructure.persistence.transaction;

import com.SecureBankingApi.domain.transaction.Transaction;
import com.SecureBankingApi.domain.transaction.TransactionRepository;
import com.SecureBankingApi.domain.transaction.TransactionStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TransactionRepositoryAdapter implements TransactionRepository {
    private SpringDataTransactionRepository repository;
    private TransactionMapper mapper;

    public TransactionRepositoryAdapter(SpringDataTransactionRepository repository, TransactionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void save(Transaction transaction) {
        TransactionJpaEntity entity = mapper.toEntity(transaction);
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        Optional<TransactionJpaEntity> entity = repository.findById(id);
        return entity.map((e) -> mapper.toDomain(e));
    }

    @Override
    public List<Transaction> findBySourceUserId(UUID userId) {
        List<TransactionJpaEntity> entities = repository.findBySourceUserId(userId);
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Transaction> findByAccountId(UUID userId) {
        List<TransactionJpaEntity> entities = repository.findByAccountId(userId);
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Transaction> findByStatus(TransactionStatus status) {
        List<TransactionJpaEntity> entities = repository.findByStatus(status);
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Transaction> findByPeriod(UUID accountId, LocalDateTime start, LocalDateTime end) {
        List<TransactionJpaEntity> entities = repository.findByPeriod(accountId, start, end);
        return mapper.toDomainList(entities);
    }
}
