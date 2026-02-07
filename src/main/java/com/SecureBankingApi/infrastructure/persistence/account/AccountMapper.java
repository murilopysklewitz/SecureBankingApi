package com.SecureBankingApi.infrastructure.persistence.account;

import com.SecureBankingApi.domain.account.Account;
import com.SecureBankingApi.domain.account.Money;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AccountMapper {
    public Account toDomain(AccountJpaEntity entity){
        return Account.restore(entity.getId(),
                entity.getUserId(),
                entity.getAccountNumber(),
                entity.getAgency(),
                Money.of(entity.getBalance()),
                entity.getStatus(),
                entity.getType(),
                entity.getCreated_at(),
                entity.getUpdated_at()
                );
    }
    public AccountJpaEntity toEntity(Account account){
        return new AccountJpaEntity(account.getId(),
                account.getAccountNumber(),
                account.getAgency(),
                account.getUserId(),
                account.getBalance().getValue(),
                account.getType(),
                account.getStatus(),
                account.getCreatedAt(),
                account.getUpdatedAt());
    }

    public List<Account> toDomainList(List<AccountJpaEntity> entities){
        return entities
                .stream()
                .map((e) -> toDomain(e))
                .collect(Collectors.toList());
    }
    public List<AccountJpaEntity> toEntities (List<Account> domains){
        return domains
                .stream()
                .map((d) -> toEntity(d))
                .collect(Collectors.toList());
    }
}
