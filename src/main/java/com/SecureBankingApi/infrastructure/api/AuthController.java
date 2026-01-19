package com.SecureBankingApi.infrastructure.api;

import com.SecureBankingApi.application.usecases.RegisterUserRequest;
import com.SecureBankingApi.application.usecases.RegisterUserResponse;
import com.SecureBankingApi.application.usecases.RegisterUserUseCase;
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
    private final RegisterUserUseCase usecase;

    public AuthController(RegisterUserUseCase usecase) {
        this.usecase = usecase;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody RegisterRequest request){
        RegisterUserRequest userRequest = new RegisterUserRequest(
                request.getFullName(),
                request.getCpf(),
                request.getEmail(),
                request.getPassword()
        );
        RegisterUserResponse response = usecase.execute(userRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
