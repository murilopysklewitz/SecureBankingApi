package com.SecureBankingApi.application.usecases;

public class RefreshTokenUserResponse {
    private final String newAccessToken;
    private final int expiresIn;

    public RefreshTokenUserResponse(String newAccessToken, int expiresIn) {
        this.newAccessToken = newAccessToken;
        this.expiresIn = expiresIn;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public String getNewAccessToken() {
        return newAccessToken;
    }
}
