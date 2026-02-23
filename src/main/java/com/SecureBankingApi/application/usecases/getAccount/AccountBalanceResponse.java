package com.SecureBankingApi.application.usecases.getAccount;

import com.SecureBankingApi.domain.account.AccountStatus;

import java.math.BigDecimal;
import java.util.UUID;


public class AccountBalanceResponse {

    private final UUID accountId;
    private final String accountNumber;
    private final BigDecimal balance;
    private final AccountStatus status;

    public AccountBalanceResponse(UUID accountId,
                                  String accountNumber,
                                  BigDecimal balance,
                                  AccountStatus status) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.status = status;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public AccountStatus getStatus() {
        return status;
    }
}
