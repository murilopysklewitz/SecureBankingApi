package com.SecureBankingApi.application.usecases;

import com.SecureBankingApi.application.services.RefreshTokenService;
import com.SecureBankingApi.domain.user.ports.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogoutUserUseCase {
    private static final Logger log = LoggerFactory.getLogger(LogoutUserUseCase.class);
    private final RefreshTokenService refreshTokenService;

    public LogoutUserUseCase(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public void execute(LogoutUserRequest request){
        refreshTokenService.revokeRefreshToken(request.getRefreshToken());
    }
}
