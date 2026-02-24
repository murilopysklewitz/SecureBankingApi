package com.SecureBankingApi.application.usecases.createTransaction;

import com.SecureBankingApi.domain.account.Account;
import com.SecureBankingApi.domain.account.AccountRepository;
import com.SecureBankingApi.domain.transaction.*;
import com.SecureBankingApi.domain.transaction.exceptions.InvalidTransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TransferMoneyUseCase {

    private static final Logger log = LoggerFactory.getLogger(TransferMoneyUseCase.class);
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransferMoneyUseCase(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public TransactionResponse execute(TransactionRequest request, UUID userId){
        Account source = accountRepository.findById(request.getSourceAccountId()).orElseThrow(() -> new RuntimeException("source account not found"));
        if(!source.getUserId().equals(userId)){
            throw new IllegalCallerException("user Id mismatch");
        }
        source.ensureActive();

        Account destination = accountRepository.findById(request.getDestinationAccountId()).orElseThrow(() -> new RuntimeException("destination account not found"));
        destination.ensureActive();

        if(destination.getUserId().equals(source.getUserId())){
            throw new InvalidTransactionException("cannot make transactions to yourself");
        }
        if(!source.hasSufficientBalance(request.getAmount())){
            throw new InvalidTransactionException("source has no sufficient balance to debit");
        }

        AccountDataTransaction sourceInfo = AccountDataTransaction.of(
                source.getUserId(),
                source.getAccountNumber(),
                source.getAgency()
        );

        AccountDataTransaction destinationInfo = AccountDataTransaction.of(
                destination.getUserId(),
                destination.getAccountNumber(),
                destination.getAgency()
        );

        Transaction transaction = Transaction.create(
                sourceInfo,
                destinationInfo,
                TransactionType.TRANSFER,
                request.getAmount(),
                null
        );
        source.debit(request.getAmount());
        destination.credit(request.getAmount());


        accountRepository.save(source);
        accountRepository.save(destination);

        transaction.completeTransaction();

        transactionRepository.save(transaction);

        return TransactionResponse.fromDomain(transaction);

    }
}
