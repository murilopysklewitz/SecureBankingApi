package com.SecureBankingApi.infrastructure.persistence.transaction;

import com.SecureBankingApi.domain.account.Money;
import com.SecureBankingApi.domain.transaction.AccountDataTransaction;
import com.SecureBankingApi.domain.transaction.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionMapper {
    public Transaction toDomain(TransactionJpaEntity entity){
        return Transaction.restore(
                entity.getId(),

                AccountDataTransaction.of(
                        entity.getDestinationUserId(),
                        entity.getDestinationAccountId(),
                        entity.getDestinationAccountNumber(),
                        entity.getDestinationAgency()),
                AccountDataTransaction.of(
                        entity.getSourceUserId(),
                        entity.getSourceAccountId(),
                        entity.getSourceAccountNumber(),
                        entity.getDestinationAgency()),

                entity.getStatus(),
                entity.getType(),
                entity.getDescription(),

                Money.of(entity.getAmount()),

                entity.getCreatedAt(),
                entity.getCompletedAt()
        );
    }

    public TransactionJpaEntity toEntity(Transaction domain){
         TransactionJpaEntity entity = new TransactionJpaEntity(
                 domain.getId(),
                 domain.getSource().getUserId(),
                 domain.getSource().getAccountId(),
                 domain.getSource().getAccountNumber(),
                 domain.getSource().getAgency(),
                 domain.getReceiver().getUserId(),
                 domain.getReceiver().getAccountId(),
                 domain.getReceiver().getAccountNumber(),
                 domain.getReceiver().getAgency(),
                 domain.getStatus(),
                 domain.getType(),
                 domain.getDescription(),
                 domain.getAmount().getValue(),
                 domain.getCreatedAt(),
                 domain.getCompletedAt()
         );
         return entity;
    }

    public List<Transaction> toDomainList(List<TransactionJpaEntity>entities){
        return entities.stream().map((e) -> toDomain(e)).toList();
    }
    public List<TransactionJpaEntity> toEntityList(List<Transaction> domains){
        return domains.stream().map((d) -> toEntity(d)).toList();
    }
}
