package com.SecureBankingApi.infrastructure.api.webDtos;

import com.SecureBankingApi.domain.account.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateAccountWebRequest {
    @NotNull
    private AccountType type;


    public void setType(AccountType type) {
        this.type = type;
    }

    public CreateAccountWebRequest() {
    }

    public AccountType getType() {
        return type;
    }
}
