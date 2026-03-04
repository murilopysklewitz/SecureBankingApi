package com.SecureBankingApi.infrastructure.api;

import com.SecureBankingApi.application.usecases.createTransaction.TransactionRequest;
import com.SecureBankingApi.application.usecases.createTransaction.TransactionResponse;
import com.SecureBankingApi.application.usecases.createTransaction.TransferMoneyUseCase;
import com.SecureBankingApi.application.usecases.depositMoney.DepositMoneyRequest;
import com.SecureBankingApi.application.usecases.depositMoney.DepositMoneyUseCase;
import com.SecureBankingApi.application.usecases.getTransactionHistory.GetTransactionHistoryUseCase;
import com.SecureBankingApi.application.usecases.getTransactionUseCase.GetTransactionUseCase;
import com.SecureBankingApi.application.usecases.reverseTransaction.ReverseTransactionRequest;
import com.SecureBankingApi.application.usecases.reverseTransaction.ReverseTransactionUseCase;
import com.SecureBankingApi.application.usecases.withdrawMoney.WithdrawMoneyRequest;
import com.SecureBankingApi.application.usecases.withdrawMoney.WithdrawMoneyUseCase;
import com.SecureBankingApi.infrastructure.api.webDtos.CreateTransactionWebRequest;
import com.SecureBankingApi.infrastructure.api.webDtos.DepositMoneyWebRequest;
import com.SecureBankingApi.infrastructure.api.webDtos.WithdrawMoneyWebRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/transaction")
@Tag(name = "Transações", description = "Operações de transferência, depósito e saque")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {
    private final TransferMoneyUseCase transferMoneyUseCase;
    private final DepositMoneyUseCase depositMoneyUseCase;
    private final WithdrawMoneyUseCase withdrawMoneyUseCase;
    private final GetTransactionHistoryUseCase getTransactionHistoryUseCase;
    private final GetTransactionUseCase getTransactionUseCase;
    private final ReverseTransactionUseCase reverseTransactionUseCase;

    public TransactionController(TransferMoneyUseCase transferMoneyUseCase,
                                 DepositMoneyUseCase depositMoneyUseCase,
                                 WithdrawMoneyUseCase withdrawMoneyUseCase,
                                 GetTransactionHistoryUseCase getTransactionHistoryUseCase,
                                 GetTransactionUseCase getTransactionUseCase,
                                 ReverseTransactionUseCase reverseTransactionUseCase) {
        this.transferMoneyUseCase = transferMoneyUseCase;
        this.depositMoneyUseCase = depositMoneyUseCase;
        this.withdrawMoneyUseCase = withdrawMoneyUseCase;
        this.getTransactionHistoryUseCase = getTransactionHistoryUseCase;
        this.getTransactionUseCase = getTransactionUseCase;
        this.reverseTransactionUseCase = reverseTransactionUseCase;
    }

    @PostMapping("/transfer")
    @Operation(
            summary = "Make transaction",
            description = "transfer money between accounts"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "transfer completed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "not enough balance"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No permission to this operation"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found"
            )
    })
    public ResponseEntity<TransactionResponse> createTransaction(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da transferência",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = TransactionResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "sourceAccountId": "550e8400-e29b-41d4-a716-446655440000",
                                              "destinationAccountNumber": "67890-1",
                                              "destinationAgency": "0001",
                                              "amount": 100.50,
                                              "description": "Payment"
                                            }
                                            """
                            )
                    )
            )

            @Valid @RequestBody CreateTransactionWebRequest request,
                                                                 @AuthenticationPrincipal UUID userId){
        TransactionRequest transactionRequest = new TransactionRequest(
                request.getSourceAccountId(),
                request.getDestinationAccountId(),
                request.getType(),
                request.getAmount()
        );

        TransactionResponse response = transferMoneyUseCase.execute(transactionRequest, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/deposit")
    @Operation(
            summary = "Make deposit",
            description = "deposit money in account"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "deposit successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "invalid data"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "no permission in this account"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "account not found"
            )
    })
    public ResponseEntity<TransactionResponse> deposit(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do depósito",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = TransactionResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "accountId": "550e8400-e29b-41d4-a716-446655440000",
                                              "amount": 500.00,
                                              "description": "Depósito inicial"
                                            }
                                            """
                            )
                    )
            )

            @Valid @RequestBody DepositMoneyWebRequest webRequest,
                                                       @AuthenticationPrincipal UUID userId){
        DepositMoneyRequest request = new DepositMoneyRequest(
                webRequest.getAccountId(),
                webRequest.getAmount(),
                webRequest.getDescription()
        );

        TransactionResponse response =  depositMoneyUseCase.execute(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @PostMapping("withdraw")
    @Operation(
            summary = "make withdraw",
            description = "withdraw money of account"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "withdraw maded"),
            @ApiResponse(responseCode = "400", description = "invalid balance"),
            @ApiResponse(responseCode = "403", description = "No permission in this account"),
            @ApiResponse(responseCode = "404", description = "account not found")
    })
    public ResponseEntity<TransactionResponse> withdraw(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "withdraw data",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = TransactionResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "accountId": "550e8400-e29b-41d4-a716-446655440000",
                                              "amount": 200.00,
                                              "description": "emergency withdraw"
                                            }
                                            """
                            )
                    )
            )

            @Valid @RequestBody WithdrawMoneyWebRequest webRequest,
                                                        @AuthenticationPrincipal UUID userId){
        WithdrawMoneyRequest request = new WithdrawMoneyRequest(
                webRequest.getAccountId(),
                webRequest.getAmount(),
                webRequest.getDescription()
        );

        TransactionResponse response = withdrawMoneyUseCase.execute(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/reverse/{transactionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionResponse> reverse(@PathVariable UUID transactionId,
                                                       @RequestBody String reason){
        ReverseTransactionRequest request = new ReverseTransactionRequest(transactionId, reason != null ? reason : "reversed by admin");
        TransactionResponse response = reverseTransactionUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/account/{accountId}")
    @Operation(
            summary = "historic of transactions",
            description = "return all the transactions of account"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "historic successfully returned"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No permission to this account"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found"
            )
    })
    public ResponseEntity<List<TransactionResponse>> getAccountHistory(
            @Parameter(description = "account Id", required = true)
            @PathVariable UUID accountId,
            @AuthenticationPrincipal UUID userId,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        List<TransactionResponse> transactions = getTransactionHistoryUseCase.execute(
                accountId,
                userId,
                isAdmin
        );

        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{transactionId}")
    @Operation(
            summary = "transaction details",
            description = "return all data of transaction"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "transaction founded"),
            @ApiResponse(responseCode = "403", description = "No permission to access this transaction"),
            @ApiResponse(responseCode = "404", description = "transaction not found")
    })
    public ResponseEntity<TransactionResponse> getTransaction(
            @Parameter(description = "transaction Id", required = true)
            @PathVariable UUID transactionId,
                                                              @AuthenticationPrincipal UUID requestingUserId,
                                                              Authentication authentication){
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        TransactionResponse response = getTransactionUseCase.execute(transactionId, requestingUserId, isAdmin);

        return ResponseEntity.ok(response);
    }

}
