package com.SecureBankingApi.infrastructure.api;

import com.SecureBankingApi.application.usecases.createTransaction.CreateTransactionUseCase;
import com.SecureBankingApi.application.usecases.createTransaction.TransactionRequest;
import com.SecureBankingApi.application.usecases.createTransaction.TransactionResponse;
import com.SecureBankingApi.application.usecases.depositMoney.DepositMoneyRequest;
import com.SecureBankingApi.application.usecases.depositMoney.DepositMoneyUseCase;
import com.SecureBankingApi.infrastructure.api.webDtos.CreateTransactionWebRequest;
import com.SecureBankingApi.infrastructure.api.webDtos.DepositMoneyWebRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {
    private final CreateTransactionUseCase createTransactionUseCase;
    private final DepositMoneyUseCase depositMoneyUseCase;

    public TransactionController(CreateTransactionUseCase createTransactionUseCase,
                                 DepositMoneyUseCase depositMoneyUseCase) {
        this.createTransactionUseCase = createTransactionUseCase;
        this.depositMoneyUseCase = depositMoneyUseCase;
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

}
