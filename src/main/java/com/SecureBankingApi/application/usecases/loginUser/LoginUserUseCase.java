package com.SecureBankingApi.application.usecases.loginUser;

import com.SecureBankingApi.application.services.JwtService;
import com.SecureBankingApi.application.services.RefreshTokenService;
import com.SecureBankingApi.domain.refreshToken.RefreshToken;
import com.SecureBankingApi.domain.user.User;
import com.SecureBankingApi.domain.user.ports.PasswordHasher;
import com.SecureBankingApi.domain.user.ports.UserRepository;
import jakarta.transaction.Transactional;

public class LoginUserUseCase {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public LoginUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher, JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public LoginUserResponse execute(LoginUserRequest request){
         User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("Invalid email or password"));

         if(!user.isActivate()){
             throw new RuntimeException("user inactive");
         }
         if(user.isBlocked()){
             throw new RuntimeException("user blocked");
         }
         boolean validPassword = passwordHasher.compare(request.getPassword(), user.getPasswordHash());

         if(!validPassword){
             throw new RuntimeException("Invalid email or password");
         }

         String accessToken = jwtService.generateAccessToken(user);

         RefreshToken refreshToken = refreshTokenService.generateRefreshToken(user.getId());

         return new LoginUserResponse(
                 accessToken,
                 refreshToken.getToken(),
                 900
         );

    }
}
