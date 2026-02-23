package com.SecureBankingApi.application.usecases.withdrawMoney;


import java.math.BigDecimal;
import java.util.UUID;

public class WithdrawMoneyRequest {
    private final UUID accountId;
    private final BigDecimal amount;
    private final String description;

    public WithdrawMoneyRequest(UUID accountId, BigDecimal amount, String description) {
        this.accountId = accountId;
        this.amount = amount;
        this.description = description;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }
}
