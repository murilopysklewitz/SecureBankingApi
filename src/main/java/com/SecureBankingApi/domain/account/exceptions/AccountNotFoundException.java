package com.SecureBankingApi.domain.account.exceptions;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {
    private final UUID accountId;
    public AccountNotFoundException(UUID accountId) {
        super("cannout found account with id: " + accountId);
        this.accountId = accountId;
    }
}
