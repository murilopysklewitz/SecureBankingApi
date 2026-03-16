package com.SecureBankingApi.application.usecases.reverseTransaction;

import com.SecureBankingApi.application.usecases.createTransaction.TransactionResponse;
import com.SecureBankingApi.domain.account.Account;
import com.SecureBankingApi.domain.account.AccountRepository;
import com.SecureBankingApi.domain.account.Money;
import com.SecureBankingApi.domain.transaction.*;
import com.SecureBankingApi.domain.transaction.exceptions.InvalidTransactionException;
import com.SecureBankingApi.domain.transaction.exceptions.TransactionNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ReverseTransactionUseCase {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public ReverseTransactionUseCase(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public TransactionResponse execute(ReverseTransactionRequest request){
        Transaction transaction = transactionRepository.findById(request.transactionId())
                .orElseThrow(() -> new TransactionNotFoundException(request.transactionId()));

        if(!transaction.isCompleted()){
            throw new InvalidTransactionException("cannot reverse a non completed transaction");
        }
        if(transaction.isReversed()){
            throw new InvalidTransactionException("cannot reverse a reversed transaction");
        }
        if (transaction.getSource() == null) {
            throw new InvalidTransactionException("Cannot reverse withdrawal: account not found");
        }
        if (transaction.getReceiver() == null) {
            throw new InvalidTransactionException("Cannot reverse deposit: account not found");
        }

        Account source = accountRepository.findByAccountNumber(transaction.getSource().getAccountNumber())
                .orElseThrow(() -> new RuntimeException("user not found"));

        Account destination = accountRepository.findByAccountNumber(transaction.getReceiver().getAccountNumber())
                .orElseThrow(() -> new RuntimeException("user not found"));

        Money amount = Money.of(transaction.getAmount().getValue());

        source.credit(amount);
        accountRepository.save(source);
        destination.debit(amount);
        accountRepository.save(destination);




        transaction.ReverseTransaction();
        transactionRepository.save(transaction);
        return TransactionResponse.fromDomain(transaction);
    }
}
