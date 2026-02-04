package com.SecureBankingApi.infrastructure.api;

import com.SecureBankingApi.application.usecases.loginUser.LoginUserRequest;
import com.SecureBankingApi.application.usecases.loginUser.LoginUserResponse;
import com.SecureBankingApi.application.usecases.loginUser.LoginUserUseCase;
import com.SecureBankingApi.application.usecases.registerUser.RegisterUserRequest;
import com.SecureBankingApi.application.usecases.registerUser.RegisterUserResponse;
import com.SecureBankingApi.application.usecases.registerUser.RegisterUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final RegisterUserUseCase registerUseCase;
    private final LoginUserUseCase loginUserUseCase;

    public AuthController(RegisterUserUseCase registerUseCase, LoginUserUseCase loginUserUseCase) {
        this.registerUseCase = registerUseCase;
        this.loginUserUseCase = loginUserUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody RegisterWebRequest request){
        RegisterUserRequest userRequest = new RegisterUserRequest(
                request.getFullName(),
                request.getCpf(),
                request.getEmail(),
                request.getPassword()
        );
        RegisterUserResponse response = registerUseCase.execute(userRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponse> login(@Valid @RequestBody LoginWebRequest request){

        LoginUserRequest useCaseRequest = new LoginUserRequest(request.getEmail(), request.getPassword());

        LoginUserResponse useCaseResponse = loginUserUseCase.execute(useCaseRequest);

        return ResponseEntity.status(HttpStatus.OK).body(useCaseResponse);
    }
}
