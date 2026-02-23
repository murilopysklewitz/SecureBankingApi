package com.SecureBankingApi.infrastructure.api;

import com.SecureBankingApi.application.usecases.createTransaction.CreateTransactionUseCase;
import com.SecureBankingApi.application.usecases.createTransaction.TransactionRequest;
import com.SecureBankingApi.application.usecases.createTransaction.TransactionResponse;
import com.SecureBankingApi.application.usecases.depositMoney.DepositMoneyRequest;
import com.SecureBankingApi.application.usecases.depositMoney.DepositMoneyUseCase;
import com.SecureBankingApi.application.usecases.getTransactionHistory.GetTransactionHistoryUseCase;
import com.SecureBankingApi.application.usecases.withdrawMoney.WithdrawMoneyRequest;
import com.SecureBankingApi.application.usecases.withdrawMoney.WithdrawMoneyUseCase;
import com.SecureBankingApi.infrastructure.api.webDtos.CreateTransactionWebRequest;
import com.SecureBankingApi.infrastructure.api.webDtos.DepositMoneyWebRequest;
import com.SecureBankingApi.infrastructure.api.webDtos.WithdrawMoneyWebRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {
    private final CreateTransactionUseCase createTransactionUseCase;
    private final DepositMoneyUseCase depositMoneyUseCase;
    private final WithdrawMoneyUseCase withdrawMoneyUseCase;
    private final GetTransactionHistoryUseCase getTransactionHistoryUseCase;

    public TransactionController(CreateTransactionUseCase createTransactionUseCase,
                                 DepositMoneyUseCase depositMoneyUseCase,
                                 WithdrawMoneyUseCase withdrawMoneyUseCase,
                                 GetTransactionHistoryUseCase getTransactionHistoryUseCase) {
        this.createTransactionUseCase = createTransactionUseCase;
        this.depositMoneyUseCase = depositMoneyUseCase;
        this.withdrawMoneyUseCase = withdrawMoneyUseCase;
        this.getTransactionHistoryUseCase = getTransactionHistoryUseCase;
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody CreateTransactionWebRequest request,
                                                                 @AuthenticationPrincipal UUID userId){
        TransactionRequest transactionRequest = new TransactionRequest(
                request.getSourceAccountId(),
                request.getDestinationAccountId(),
                request.getType(),
                request.getAmount()
        );

        TransactionResponse response = createTransactionUseCase.execute(transactionRequest, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@Valid @RequestBody DepositMoneyWebRequest webRequest,
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
    public ResponseEntity<TransactionResponse> withdraw(@Valid @RequestBody WithdrawMoneyWebRequest webRequest,
                                                        @AuthenticationPrincipal UUID userId){
        WithdrawMoneyRequest request = new WithdrawMoneyRequest(
                webRequest.getAccountId(),
                webRequest.getAmount(),
                webRequest.getDescription()
        );

        TransactionResponse response = withdrawMoneyUseCase.execute(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getAccountHistory(
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

}
