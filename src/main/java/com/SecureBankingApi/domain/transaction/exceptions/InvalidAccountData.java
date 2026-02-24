package com.SecureBankingApi.domain.transaction.exceptions;

public class InvalidAccountData extends RuntimeException {
    public InvalidAccountData(String message) {
        super(message);
    }
}
