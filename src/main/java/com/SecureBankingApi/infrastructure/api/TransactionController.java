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
import com.SecureBankingApi.domain.PageRequest;
import com.SecureBankingApi.domain.PageResult;
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
import org.springframework.data.domain.Page;
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

    public ResponseEntity<TransactionResponse> createTransaction(
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
    public ResponseEntity<TransactionResponse> deposit(
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

    public ResponseEntity<TransactionResponse> withdraw(

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
    public ResponseEntity<PageResult<TransactionResponse>> getAccountHistory(
            @Parameter(description = "account Id", required = true)
            @PathVariable UUID accountId,
            @AuthenticationPrincipal UUID userId,
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10")        int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc")      String sortDirection) {

        if (size > 50) size = 50;

        boolean isAdmin = authentication.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        PageRequest pageRequest = new PageRequest(sortBy, sortDirection, size, page);

        PageResult<TransactionResponse> response = getTransactionHistoryUseCase
                .execute(accountId, userId, isAdmin, pageRequest);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{transactionId}")
    @Operation(
            summary = "transaction details",
            description = "return all data of transaction"
    )
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
