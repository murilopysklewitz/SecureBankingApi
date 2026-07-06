package com.SecureBankingApi.application.usecases.createAccount;

import com.SecureBankingApi.domain.account.AccountType;

import java.util.UUID;

public class CreateAccountRequest {
    private UUID userId;
    private String email;
    private AccountType type;

    public CreateAccountRequest(UUID userId, String email, AccountType type) {
        this.userId = userId;
        this.email = email;
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public UUID getUserId() {
        return userId;
    }

    public AccountType getType() {
        return type;
    }

}
