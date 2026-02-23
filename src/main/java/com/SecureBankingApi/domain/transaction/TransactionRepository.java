package com.SecureBankingApi.domain.transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {
    void save(Transaction transaction);
    Optional<Transaction> findById(UUID id);
    List<Transaction> findBySourceUserId(UUID userId);
    List<Transaction> findByAccountId(UUID accountId);

    List<Transaction> findByStatus(TransactionStatus status);

    List<Transaction> findByPeriod(UUID accountId, LocalDateTime start, LocalDateTime end);

}
