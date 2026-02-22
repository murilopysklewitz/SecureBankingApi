package com.SecureBankingApi.domain.transaction;

import com.SecureBankingApi.domain.account.Money;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private UUID id;
    private final AccountDataTransaction receiver;
    private final AccountDataTransaction source;


    private TransactionStatus status;
    private final TransactionType type;

    private String description;
    private Money amount;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    private Transaction(AccountDataTransaction receiver,
                       AccountDataTransaction source,
                       TransactionType type,
                       Money amount,
                       String description) {
        this.id = null;
        this.completedAt = null;

        this.receiver = receiver;
        this.source = source;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        this.status = TransactionStatus.PENDING;
        this.amount = amount;
        this.description = description;
    }

    public static Transaction create(AccountDataTransaction receiver,
                                     AccountDataTransaction source,
                                     TransactionType type,
                                     Money amount) {

        if (receiver == null) {
            throw new RuntimeException("receiver cannot be null");
        }
        if (source == null) {
            throw new RuntimeException("source cannot be null");
        }
        if (type == null) {
            throw new RuntimeException("type cannot be null");
        }
        if (amount == null) {
            throw new RuntimeException("amount cannot be null");
        }
        return new Transaction(receiver, source, type, amount, null);
    }

    public static Transaction restore(UUID id,
                                      AccountDataTransaction receiver,
                                      AccountDataTransaction source,
                                      TransactionStatus status,
                                      TransactionType type,
                                      String description,
                                      Money amount,
                                      LocalDateTime createdAt,
                                      LocalDateTime completedAt){
        Transaction transaction = new Transaction(receiver, source, type, amount, null);
        transaction.id = id;
        transaction.description = description;
        transaction.status = status;
        transaction.completedAt =completedAt;
        transaction.createdAt = createdAt;

        return transaction;
    }

    public void completeTransaction(){
        ensureStatusPending();
        this.status = TransactionStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
    public void failTransaction(){
        ensureStatusPending();
        this.status = TransactionStatus.FAILED;
    }
    public void ReverseTransaction() {
        if(this.status != TransactionStatus.COMPLETED){
            throw new RuntimeException("cannot reverse a incompleted transaction");
        }
        this.status = TransactionStatus.REVERSED;
    }

    public boolean isReversed() {
        return this.status == TransactionStatus.REVERSED;
    }
    public boolean isFailed() {
        return this.status == TransactionStatus.FAILED;
    }
    public boolean isCompleted() {
        return  this.status == TransactionStatus.COMPLETED;
    }

    public UUID getId() {
        return id;
    }

    public AccountDataTransaction getReceiver() {
        return receiver;
    }

    public AccountDataTransaction getSource() {
        return source;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public TransactionType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public Money getAmount() {
        return amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    private void ensureStatusPending(){
        if(this.status == TransactionStatus.COMPLETED){
            throw new RuntimeException("Transaction completed");
        }
        if(this.status == TransactionStatus.REVERSED){
            throw new RuntimeException("transaction reversed");
        }
        if(this.status == TransactionStatus.FAILED){
            throw new RuntimeException("transaction failed");
        }
    }

}