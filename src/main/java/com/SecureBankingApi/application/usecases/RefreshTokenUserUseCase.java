package com.SecureBankingApi.application.usecases;

import com.SecureBankingApi.application.exceptions.UserBlockedException;
import com.SecureBankingApi.application.exceptions.UserInactiveException;
import com.SecureBankingApi.application.exceptions.UserInactiveException;
import com.SecureBankingApi.application.exceptions.UserNotFoundException;
import com.SecureBankingApi.application.services.JwtService;
import com.SecureBankingApi.application.services.RefreshTokenService;
import com.SecureBankingApi.domain.RefreshToken;
import com.SecureBankingApi.domain.user.User;
import com.SecureBankingApi.domain.user.ports.UserRepository;
import jakarta.transaction.Transactional;

public class RefreshTokenUserUseCase {
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public RefreshTokenUserUseCase(RefreshTokenService refreshTokenService, JwtService jwtService, UserRepository userRepository) {
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Transactional
    public RefreshTokenUserResponse execute(RefreshTokenUserRequest request){
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(request.getTokenString());
        User user = userRepository.findById(refreshToken.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found"));

        if(!user.isActivate()){
            refreshTokenService.revokeRefreshToken(request.getTokenString());
            throw new UserInactiveException("User inactive");
        }
        if(user.isBlocked()){
            refreshTokenService.revokeRefreshToken(request.getTokenString());
            throw new UserBlockedException("User blocked");
        }

        String newAccessToken = jwtService.generateAccessToken(user);

        return new RefreshTokenUserResponse(newAccessToken, 900);
    }
}
