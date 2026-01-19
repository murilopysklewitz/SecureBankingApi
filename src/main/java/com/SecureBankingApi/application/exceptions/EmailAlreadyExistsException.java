package com.SecureBankingApi.application.exceptions;

public class EmailAlreadyExistsException extends RuntimeException {
    private final String email;

    public EmailAlreadyExistsException(String email) {
        super("Email already exists " + email);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
