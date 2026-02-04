package com.SecureBankingApi.infrastructure.api.webDtos;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenWebRequest {

    @NotBlank
    private String refreshToken;

    protected RefreshTokenWebRequest() {}

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
