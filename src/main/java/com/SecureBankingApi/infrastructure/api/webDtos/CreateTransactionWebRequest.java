package com.SecureBankingApi.infrastructure.api.webDtos;

import com.SecureBankingApi.domain.account.Money;
import com.SecureBankingApi.domain.transaction.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateTransactionWebRequest {
    @NotBlank
    private UUID sourceAccountId;
    @NotBlank
    private UUID destinationAccountId;
    @NotBlank
    private TransactionType type;
    @NotBlank
    private Money amount;

    public CreateTransactionWebRequest(UUID sourceAccountId, UUID destinationAccountId, TransactionType type, Money amount) {
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
