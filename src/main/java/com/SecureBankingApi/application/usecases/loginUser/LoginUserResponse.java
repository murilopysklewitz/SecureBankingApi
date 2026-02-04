package com.SecureBankingApi.application.usecases.loginUser;

public class LoginUserResponse {
private final String accessToken;
private final String refreshToken;
private final int expiresIn;

    public LoginUserResponse(String accessToken, String refreshToken, int expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }
}
