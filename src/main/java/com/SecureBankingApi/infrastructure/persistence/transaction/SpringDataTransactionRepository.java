package com.SecureBankingApi.infrastructure.persistence.transaction;

import com.SecureBankingApi.domain.transaction.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SpringDataTransactionRepository extends JpaRepository<TransactionJpaEntity, UUID> {
    List<TransactionJpaEntity> findBySourceUserId(UUID userId);
    List<TransactionJpaEntity> findByDestinationUserId(UUID userId);

    List<TransactionJpaEntity> findByStatus(TransactionStatus status);

    @Query("SELECT t FROM TransactionJpaEntity t WHERE " +
            "(t.sourceUserId = :accountId OR t.destinationUserId = :accountId) " +
            "AND t.createdAt BETWEEN :start AND :end")
    List<TransactionJpaEntity> findByPeriod(
            @Param("accountId") UUID accountId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
