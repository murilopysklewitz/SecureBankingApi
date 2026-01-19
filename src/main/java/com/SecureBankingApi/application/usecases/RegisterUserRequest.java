package com.SecureBankingApi.application.usecases;

public class RegisterUserRequest {
    private final String fullName;
    private final String cpf;
    private final String email;
    private final String password;

    public RegisterUserRequest(String fullName, String cpf, String email, String password) {
        this.fullName = fullName;
        this.cpf = cpf;
        this.email = email;
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

}
