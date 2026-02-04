package com.SecureBankingApi.application.usecases.loginUser;

public class LoginUserRequest {

    private String email;

    private String password;

    public LoginUserRequest(String email,  String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
