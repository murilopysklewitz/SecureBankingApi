package com.SecureBankingApi.infrastructure.api;

import com.SecureBankingApi.application.usecases.createAccount.AccountResponse;
import com.SecureBankingApi.application.usecases.createAccount.CreateAccountRequest;
import com.SecureBankingApi.application.usecases.createAccount.CreateAccountUseCase;
import com.SecureBankingApi.application.usecases.getAccount.AccountBalanceResponse;
import com.SecureBankingApi.application.usecases.getAccount.GetAccountBalanceUseCase;
import com.SecureBankingApi.application.usecases.getAccount.GetAccountDetailsUseCase;
import com.SecureBankingApi.application.usecases.getAccount.ListMyAccountsUseCase;
import com.SecureBankingApi.application.usecases.modifyStatusAccount.BlockAccountUseCase;
import com.SecureBankingApi.application.usecases.modifyStatusAccount.CloseAccountUseCase;
import com.SecureBankingApi.application.usecases.modifyStatusAccount.UnblockAccountUseCase;
import com.SecureBankingApi.infrastructure.api.webDtos.CreateAccountWebRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Accounts", description = "Management of Banking accounts")
@SecurityRequirement(name = "bearerAuth")
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

    @Operation(
            summary = "Create new account",
            description = "Create account to an auth user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "account created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "id": "550e8400-e29b-41d4-a716-446655440000",
                                              "accountNumber": "12345-6",
                                              "agency": "0001",
                                              "accountType": "CHECKING",
                                              "balance": 0.00,
                                              "status": "ACTIVE",
                                              "createdAt": "2024-03-02T10:30:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })


    public ResponseEntity<AccountResponse> createAccount(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "create account data",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateAccountRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "accountType": "CHECKING",
                                              "agency": "0001"
                                            }
                                            """
                            )
                    )
            )

            @Valid @RequestBody CreateAccountWebRequest request,
                                                         @AuthenticationPrincipal UUID userId){
        CreateAccountRequest useCaseRequest = new CreateAccountRequest(userId, request.getType());

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
    @Operation(
            summary = "Find account details",
            description = "Return account details"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Account founded",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "cannot access this account"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found"
            )
    })
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

