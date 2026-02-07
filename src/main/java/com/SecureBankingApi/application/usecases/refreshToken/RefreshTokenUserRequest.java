package com.SecureBankingApi.application.usecases.refreshToken;

public class RefreshTokenUserRequest {
    private final String refreshToken;

    public RefreshTokenUserRequest( String refreshToken) {
        this.refreshToken = refreshToken;
    }



    public String getTokenString() {
        return refreshToken;
    }
}
