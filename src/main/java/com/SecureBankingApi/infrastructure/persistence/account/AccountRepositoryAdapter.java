package com.SecureBankingApi.infrastructure.persistence.account;

import com.SecureBankingApi.domain.account.Account;
import com.SecureBankingApi.domain.account.AccountRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AccountRepositoryAdapter implements AccountRepository {
    private final SpringDataAccountRepository repository;
    private final AccountMapper mapper;

    public AccountRepositoryAdapter(SpringDataAccountRepository repository,
                                    AccountMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void save(Account account) {
        AccountJpaEntity account1 = mapper.toEntity(account);
        repository.save(account1);
    }

    @Override
    public List<Account> findAll() {
        List<AccountJpaEntity> entities = repository.findAll();
        return mapper.toDomainList(entities);
    }

    @Override
    public Optional<Account> findById(UUID id) {
        return repository
                .findById(id)
                .map((e) -> mapper.toDomain(e));
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return repository.findByAccountNumber(accountNumber).map((e) -> mapper.toDomain(e));
    }

    @Override
    public List<Account> findByUserId(UUID userId) {
        return repository.findByUserId(userId).stream().map((e) -> mapper.toDomain(e)).toList();
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return repository.existsByAccountNumber(accountNumber);
    }

    @Override
    public void delete(Account account) {
        repository.deleteById(account.getId());
    }
}
