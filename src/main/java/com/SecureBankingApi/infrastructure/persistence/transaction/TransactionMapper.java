package com.SecureBankingApi.infrastructure.persistence.transaction;

import com.SecureBankingApi.domain.account.AccountNumber;
import com.SecureBankingApi.domain.account.Money;
import com.SecureBankingApi.domain.transaction.AccountDataTransaction;
import com.SecureBankingApi.domain.transaction.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class TransactionMapper {
    public Transaction toDomain(TransactionJpaEntity entity){
        return Transaction.restore(
                entity.getId(),

                AccountDataTransaction.of(
                        entity.getDestinationUserId(),
                        entity.getDestinationAccountId(),
                        AccountNumber.restore(entity.getDestinationAccountNumber()),
                        entity.getDestinationAgency()),
                AccountDataTransaction.of(
                        entity.getSourceUserId(),
                        entity.getSourceAccountId(),
                        AccountNumber.restore(entity.getSourceAccountNumber()),
                        entity.getSourceAgency()),

                entity.getStatus(),
                entity.getType(),
                entity.getDescription(),

                Money.of(entity.getAmount()),

                entity.getCreatedAt(),
                entity.getCompletedAt()
        );
    }

    public TransactionJpaEntity toEntity(Transaction domain){
        UUID sourceUserId = domain.getSource() != null ? domain.getSource().getUserId() : null;
         TransactionJpaEntity entity = new TransactionJpaEntity(
                 domain.getId(),
                 domain.getSource().getUserId(),
                 domain.getSource().getAccountId(),
                 domain.getSource().getAccountNumber().getValue(),
                 domain.getSource().getAgency(),
                 domain.getReceiver().getUserId(),
                 domain.getReceiver().getAccountId(),
                 domain.getReceiver().getAccountNumber().getValue(),
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
