package com.SecureBankingApi.domain.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

public class Account {
    private UUID id;
    private String accountNumber;
    private String agency;
    private UUID userId;
    private Money balance;
    private AccountType type;
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Account(String accountNumber, String agency, UUID userId, Money balance, AccountType type, AccountStatus status) {
        this.id = null;
        this.accountNumber = accountNumber;
        this.agency = agency;
        this.userId = userId;
        this.balance = balance;
        this.type = type;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Account create(String accountNumber, String agency, UUID userId, AccountType type){
        if(accountNumber == null || accountNumber.isBlank()){
            throw new IllegalArgumentException("account number cannot be null");
        }
        if(agency == null || agency.isBlank()){
            throw  new IllegalArgumentException("agency cannot be null");
        }
        if(userId == null){
            throw new IllegalArgumentException("user id cannot be null");
        }
        if(type == null){
            throw new IllegalArgumentException("account type cannot be null");
        }
        Money balance = Money.zero();

        return new Account(accountNumber, agency, userId, balance, type, AccountStatus.ACTIVE);
    }

    public static Account restore(UUID id, UUID userId, LocalDateTime createdAt, LocalDateTime updatedAt, AccountStatus status, AccountType type, Money balance, String agency, String accountNumber) {
        Account account = new Account(accountNumber, agency, userId, balance, type, status);
        account.id = id;
        account.createdAt =createdAt;
        account.updatedAt = updatedAt;

        return account;
    }

    public void credit(Money amount){
        ensureActive();

        if(amount == null){
            throw new IllegalArgumentException("amount cannot be null");
        }
        this.balance.add(amount);
        touch();
    }

    public void debit(Money amount){
        ensureActive();

        if(amount == null){
            throw new IllegalArgumentException("amount cannot be null");
        }
        this.balance.subtract(amount);
        touch();
    }

    public boolean hasSufficientBalance(Money amount){
        return this.balance.getValue().compareTo(amount.getValue()) >= 0;
    }


    public void block(){
        this.status = AccountStatus.BLOCKED;
    }

    public void close(){
        ensureActive();

        if(!this.balance.isZero()){
            throw new IllegalArgumentException("Cannot close account with balance");
        }
    }


    public void ensureActive() {
        if(this.status == AccountStatus.BLOCKED){
            throw new IllegalArgumentException("Account is blocked");
        }
        if(this.status == AccountStatus.INACTIVE){
            throw new IllegalArgumentException("Account is inative");
        }

    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }


    public boolean isClosed() {
        return this.status == AccountStatus.BLOCKED;
    }

    public boolean isActive(){
        return this.status == AccountStatus.ACTIVE;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public AccountType getType() {
        return type;
    }

    public Money getBalance() {
        return balance;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getAgency() {
        return agency;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public UUID getId() {
        return id;
    }
}
