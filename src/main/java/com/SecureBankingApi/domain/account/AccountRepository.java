package com.SecureBankingApi.domain.account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    void save(Account account);
    List<Account> findAll();
    Optional<Account> findById(UUID id);
    Optional<Account> findByAccountNumber(String accountNumber);
    Optional<Account> findByUserId(UUID userId);
    boolean existsByAccountNumber(String accountNumber);
    void delete(Account account);

}
