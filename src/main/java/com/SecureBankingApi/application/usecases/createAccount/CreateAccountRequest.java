package com.SecureBankingApi.application.usecases.createAccount;

import com.SecureBankingApi.domain.account.AccountType;

import java.util.UUID;

public class CreateAccountRequest {
    private UUID userId;
    private AccountType type;

    public CreateAccountRequest(UUID userId, AccountType type) {
        this.userId = userId;
        this.type = type;
    }

    public UUID getUserId() {
        return userId;
    }

    public AccountType getType() {
        return type;
    }

}
