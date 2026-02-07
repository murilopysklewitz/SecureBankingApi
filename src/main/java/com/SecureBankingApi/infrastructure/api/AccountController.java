package com.SecureBankingApi.infrastructure.api;

import com.SecureBankingApi.application.usecases.createAccount.AccountResponse;
import com.SecureBankingApi.application.usecases.createAccount.CreateAccountRequest;
import com.SecureBankingApi.application.usecases.createAccount.CreateAccountUseCase;
import com.SecureBankingApi.infrastructure.api.webDtos.CreateAccountWebRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController("/api/accounts")
public class AccountController {
    private final CreateAccountUseCase createAccountUseCase;

    public AccountController(CreateAccountUseCase createAccountUseCase) {
        this.createAccountUseCase = createAccountUseCase;
    }

    @PostMapping("/create")
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountWebRequest request,
                                                         @AuthenticationPrincipal UUID userId){
        CreateAccountRequest useCaseRequest = new CreateAccountRequest(request.getUserId(), request.getType());

        AccountResponse response = createAccountUseCase.execute(useCaseRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
