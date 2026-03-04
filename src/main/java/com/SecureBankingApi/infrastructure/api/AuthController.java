package com.SecureBankingApi.infrastructure.api;

import com.SecureBankingApi.application.usecases.loginUser.LoginUserRequest;
import com.SecureBankingApi.application.usecases.loginUser.LoginUserResponse;
import com.SecureBankingApi.application.usecases.loginUser.LoginUserUseCase;
import com.SecureBankingApi.application.usecases.logoutUser.LogoutUserRequest;
import com.SecureBankingApi.application.usecases.logoutUser.LogoutUserUseCase;
import com.SecureBankingApi.application.usecases.refreshToken.RefreshTokenUserRequest;
import com.SecureBankingApi.application.usecases.refreshToken.RefreshTokenUserResponse;
import com.SecureBankingApi.application.usecases.refreshToken.RefreshTokenUserUseCase;
import com.SecureBankingApi.application.usecases.registerUser.RegisterUserRequest;
import com.SecureBankingApi.application.usecases.registerUser.RegisterUserResponse;
import com.SecureBankingApi.application.usecases.registerUser.RegisterUserUseCase;
import com.SecureBankingApi.infrastructure.api.webDtos.LoginWebRequest;
import com.SecureBankingApi.infrastructure.api.webDtos.LogoutWebRequest;
import com.SecureBankingApi.infrastructure.api.webDtos.RefreshTokenWebRequest;
import com.SecureBankingApi.infrastructure.api.webDtos.RegisterWebRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints to register and login users")
public class AuthController {
    private final RegisterUserUseCase registerUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final RefreshTokenUserUseCase refreshTokenUserUseCase;
    private final LogoutUserUseCase logoutUserUseCase;

    public AuthController(RegisterUserUseCase registerUseCase,
                          LoginUserUseCase loginUserUseCase,
                          RefreshTokenUserUseCase refreshTokenUserUseCase,
                          LogoutUserUseCase logoutUserUseCase) {
        this.registerUseCase = registerUseCase;
        this.loginUserUseCase = loginUserUseCase;
        this.refreshTokenUserUseCase = refreshTokenUserUseCase;
        this.logoutUserUseCase = logoutUserUseCase;
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register new User",
            description = "Create a new User with CPF, email and password"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class),
                                    examples = @ExampleObject(
                                            value = "\"User registered successfully\""
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid data(CPF already exists), invalid email",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\"error\": \"CPF already exists\"}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal error "
                    )
            }
    )
    public ResponseEntity<RegisterUserResponse> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(

            description = "user data to register",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = RegisterWebRequest.class),
                    examples = @ExampleObject(
                            value = """
                                            {
                                              "cpf": "12345678901",
                                              "email": "usuario@example.com",
                                              "password": "securePassword123!",
                                              "name": "Joao Pedrao"
                                            }
                                            """
                    )
            )

    )@Valid @RequestBody RegisterWebRequest request){
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
    @Operation(
            summary = "Login",
            description = "Auth users and return JWT token"
    ) @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginUserResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                              "type": "Bearer",
                                              "expiresIn": 3600
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "invalid credentials",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"error\": \"Invalid credentials\"}"
                            )
                    )
            )
    })
    public ResponseEntity<LoginUserResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "CPF and Password",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoginUserRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "cpf": "12345678901",
                                              "password": "SecurePassword123!"
                                            }
                                            """
                            )
                    )
            )

            @Valid @RequestBody LoginWebRequest request){

        LoginUserRequest useCaseRequest = new LoginUserRequest(request.getEmail(), request.getPassword());

        LoginUserResponse useCaseResponse = loginUserUseCase.execute(useCaseRequest);

        return ResponseEntity.status(HttpStatus.OK).body(useCaseResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenUserResponse> refreshToken(@Valid @RequestBody RefreshTokenWebRequest request){
        RefreshTokenUserRequest useCaseRequest = new RefreshTokenUserRequest(request.getRefreshToken());

        RefreshTokenUserResponse response = refreshTokenUserUseCase.execute(useCaseRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/revoke")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutWebRequest request){
        LogoutUserRequest useCaseRequest = new LogoutUserRequest(request.getRefreshToken());

        logoutUserUseCase.execute(useCaseRequest);

        return ResponseEntity.noContent().build();
    }
}
