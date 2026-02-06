package com.SecureBankingApi.infrastructure.persistence;

import com.SecureBankingApi.domain.account.AccountStatus;
import com.SecureBankingApi.domain.account.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataAccountRepository extends JpaRepository<AccountJpaEntity, UUID> {
    Optional<AccountJpaEntity> findByAccountNumber(String accountNumber);
    Optional<AccountJpaEntity> findByUserId(UUID userId);
    List<AccountJpaEntity> findByStatus(AccountStatus status);
    List<AccountJpaEntity> findByType(AccountType type);

    boolean existsByAccountNumber(String accountNumber);
}
