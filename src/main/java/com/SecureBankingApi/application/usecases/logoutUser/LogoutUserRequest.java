package com.SecureBankingApi.application.usecases.logoutUser;

public class LogoutUserRequest {
    private final String refreshToken;

    public LogoutUserRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
