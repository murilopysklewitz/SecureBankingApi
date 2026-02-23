package com.SecureBankingApi.infrastructure.api.webDtos;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.UUID;

public class DepositMoneyWebRequest {
    @NotBlank
    private final UUID accountId;
    @NotBlank
    private final BigDecimal amount;
    @NotBlank
    private final String description;

    public DepositMoneyWebRequest(UUID accountId, BigDecimal amount, String description) {
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
