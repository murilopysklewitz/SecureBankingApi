package com.SecureBankingApi.application.exceptions;

public class CpfAlreadyExistsException extends RuntimeException {
    private final String cpf;
    public CpfAlreadyExistsException(String cpf) {
        super("cpf already exists ");
        this.cpf = cpf;
    }

    public String getCpf() {
        return cpf;
    }
}
