package com.SecureBankingApi.application.usecases.createAccount;

import com.SecureBankingApi.domain.account.*;

import java.time.LocalDateTime;
import java.util.UUID;

public class AccountResponse {
    private UUID id;
    private AccountNumber accountNumber;
    private String agency;
    private UUID userId;
    private Money balance;
    private AccountType type;
    private AccountStatus status;
    private LocalDateTime createdAt;

    public AccountResponse(UUID id,
                           AccountNumber accountNumber,
                           String agency,
                           UUID userId,
                           Money balance,
                           AccountType type,
                           AccountStatus status,
                           LocalDateTime createdAt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.agency = agency;
        this.userId = userId;
        this.balance = balance;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
    }
    public static AccountResponse fromDomain(Account account){
        return new AccountResponse(account.getId(),
                account.getAccountNumber(),
                account.getAgency(),
                account.getUserId(),
                account.getBalance(),
                account.getType(),
                account.getStatus(),
                account.getCreatedAt());
    }

    public UUID getId() {
        return id;
    }

    public AccountNumber getAccountNumber() {
        return accountNumber;
    }

    public String getAgency() {
        return agency;
    }

    public UUID getUserId() {
        return userId;
    }

    public Money getBalance() {
        return balance;
    }

    public AccountType getType() {
        return type;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
