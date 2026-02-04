package com.SecureBankingApi.infrastructure.api.webDtos;

import jakarta.validation.constraints.NotBlank;

public class LogoutWebRequest {

    @NotBlank
    private String refreshToken;

    protected LogoutWebRequest() {
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
