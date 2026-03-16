package com.SecureBankingApi.domain.account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    void save(Account account);
    List<Account> findAll();
    Optional<Account> findById(UUID id);
    Optional<Account> findByAccountNumber(AccountNumber accountNumber);
    List<Account> findByUserId(UUID userId);
    List<Account> findByStatus(AccountStatus status);
    List<Account> findByType(AccountType type);
    boolean existsByAccountNumber(AccountNumber accountNumber);
    boolean existsByUserIdAndType(UUID userId, AccountType type);
    void delete(Account account);

}
