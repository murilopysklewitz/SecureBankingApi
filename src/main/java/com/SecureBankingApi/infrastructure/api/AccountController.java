package com.SecureBankingApi.infrastructure.api;

import com.SecureBankingApi.application.usecases.*;
import com.SecureBankingApi.application.usecases.createAccount.AccountResponse;
import com.SecureBankingApi.application.usecases.createAccount.CreateAccountRequest;
import com.SecureBankingApi.application.usecases.createAccount.CreateAccountUseCase;
import com.SecureBankingApi.infrastructure.api.webDtos.CreateAccountWebRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController("/api/accounts")
public class AccountController {
    private final CreateAccountUseCase createAccountUseCase;
    private final ListMyAccountsUseCase listMyAccountsUseCase;
    private final GetAccountDetailsUseCase getAccountDetailsUseCase;
    private final GetAccountBalanceUseCase getAccountBalanceUseCase;
    private final BlockAccountUseCase blockAccountUseCase;
    private final UnblockAccountUseCase unblockAccountUseCase;
    private final CloseAccountUseCase closeAccountUseCase;

    public AccountController(CreateAccountUseCase createAccountUseCase,
                             ListMyAccountsUseCase listMyAccountsUseCase,
                             GetAccountDetailsUseCase getAccountDetailsUseCase,
                             GetAccountBalanceUseCase getAccountBalanceUseCase,
                             BlockAccountUseCase blockAccountUseCase,
                             UnblockAccountUseCase unblockAccountUseCase,
                             CloseAccountUseCase closeAccountUseCase) {
        this.createAccountUseCase = createAccountUseCase;
        this.listMyAccountsUseCase = listMyAccountsUseCase;
        this.getAccountDetailsUseCase = getAccountDetailsUseCase;
        this.getAccountBalanceUseCase = getAccountBalanceUseCase;
        this.blockAccountUseCase = blockAccountUseCase;
        this.unblockAccountUseCase = unblockAccountUseCase;
        this.closeAccountUseCase = closeAccountUseCase;
    }

    @PostMapping("/create")
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountWebRequest request,
                                                         @AuthenticationPrincipal UUID userId){
        CreateAccountRequest useCaseRequest = new CreateAccountRequest(request.getUserId(), request.getType());

        AccountResponse response = createAccountUseCase.execute(useCaseRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> listMyAccounts(
            @AuthenticationPrincipal UUID userId) {

        List<AccountResponse> accounts = listMyAccountsUseCase.execute(userId);

        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountDetails(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        AccountResponse response = getAccountDetailsUseCase.execute(id, userId, isAdmin);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<AccountBalanceResponse> getAccountBalance(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        AccountBalanceResponse response = getAccountBalanceUseCase.execute(id, userId, isAdmin);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> blockAccount(@PathVariable UUID id) {

        blockAccountUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> unblockAccount(@PathVariable UUID id) {

        unblockAccountUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> closeAccount(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        closeAccountUseCase.execute(id, userId, isAdmin);

        return ResponseEntity.noContent().build();
    }
}

