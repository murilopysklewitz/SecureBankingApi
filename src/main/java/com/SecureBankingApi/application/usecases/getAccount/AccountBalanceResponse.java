package com.SecureBankingApi.application.usecases.getAccount;

import com.SecureBankingApi.domain.account.AccountNumber;
import com.SecureBankingApi.domain.account.AccountStatus;

import java.math.BigDecimal;
import java.util.UUID;


public class AccountBalanceResponse {

    private final UUID accountId;
    private final AccountNumber accountNumber;
    private final BigDecimal balance;
    private final AccountStatus status;

    public AccountBalanceResponse(UUID accountId,
                                  AccountNumber accountNumber,
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

    public AccountNumber getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public AccountStatus getStatus() {
        return status;
    }
}
