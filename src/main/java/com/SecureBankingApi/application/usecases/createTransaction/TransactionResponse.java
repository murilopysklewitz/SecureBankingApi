package com.SecureBankingApi.application.usecases.createTransaction;

import com.SecureBankingApi.domain.account.Money;
import com.SecureBankingApi.domain.transaction.AccountDataTransaction;
import com.SecureBankingApi.domain.transaction.Transaction;
import com.SecureBankingApi.domain.transaction.TransactionStatus;
import com.SecureBankingApi.domain.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionResponse {
    private final UUID id;
    private final UUID sourceUserId;
    private final String sourceAccountNumber;
    private final String sourceAgency;
    private final UUID destinationUserId;
    private final String destinationAccountNumber;
    private final String destinationAgency;
    private final BigDecimal amount;
    private final TransactionType type;
    private final TransactionStatus status;
    private final String description;
    private final LocalDateTime createdAt;
    private final LocalDateTime completedAt;

    public TransactionResponse(UUID id,
                               UUID sourceUserId,
                               String sourceAccountNumber,
                               String sourceAgency,
                               UUID destinationUserId,
                               String destinationAccountNumber,
                               String destinationAgency,
                               BigDecimal amount,
                               TransactionType type,
                               TransactionStatus status,
                               String description,
                               LocalDateTime createdAt,
                               LocalDateTime completedAt) {
        this.id = id;

        this.sourceUserId = sourceUserId;
        this.sourceAccountNumber = sourceAccountNumber;
        this.sourceAgency = sourceAgency;
        this.destinationUserId = destinationUserId;
        this.destinationAccountNumber = destinationAccountNumber;
        this.destinationAgency = destinationAgency;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.description = description;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

    public static TransactionResponse fromDomain(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getSource().getUserId(),
                transaction.getSource().getAccountNumber(),
                transaction.getSource().getAgency(),
                transaction.getReceiver().getUserId(),
                transaction.getReceiver().getAccountNumber(),
                transaction.getReceiver().getAgency(),
                transaction.getAmount().getValue(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getDescription(),
                transaction.getCreatedAt(),
                transaction.getCompletedAt()
        );
    }

    public UUID getId() { return id; }
    public UUID getSourceUserId() { return sourceUserId; }
    public String getSourceAccountNumber() { return sourceAccountNumber; }
    public String getSourceAgency() { return sourceAgency; }
    public UUID getReceiverUserId() { return destinationUserId; }
    public String getReceiverAccountNumber() { return destinationAccountNumber; }
    public String getReceiverAgency() { return destinationAgency; }
    public BigDecimal getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public TransactionStatus getStatus() { return status; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
}
