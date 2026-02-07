package com.SecureBankingApi.infrastructure.api.webDtos;

import com.SecureBankingApi.domain.account.AccountType;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public class CreateAccountWebRequest {
    @NotBlank
    private UUID userId;
    @NotBlank
    private AccountType type;

    public CreateAccountWebRequest() {
    }

    public UUID getUserId() {
        return userId;
    }

    public AccountType getType() {
        return type;
    }
}
