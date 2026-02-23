package com.SecureBankingApi.application.usecases.createTransaction;

import com.SecureBankingApi.domain.account.Money;
import com.SecureBankingApi.domain.transaction.AccountDataTransaction;
import com.SecureBankingApi.domain.transaction.TransactionType;

import java.util.UUID;

public class TransactionRequest {
    private UUID sourceAccountId;
    private UUID destinationAccountId;
    private TransactionType type;
    private Money amount;

    public TransactionRequest(UUID sourceAccountId, UUID destinationAccountId, TransactionType type, Money amount) {
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.type = type;
        this.amount = amount;
    }

    public UUID getSourceAccountId() {
        return sourceAccountId;
    }

    public UUID getDestinationAccountId() {
        return destinationAccountId;
    }

    public TransactionType getType() {
        return type;
    }

    public Money getAmount() {
        return amount;
    }
}
