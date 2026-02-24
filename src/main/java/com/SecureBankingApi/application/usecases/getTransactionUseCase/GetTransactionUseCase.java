package com.SecureBankingApi.application.usecases.getTransactionUseCase;

import com.SecureBankingApi.application.usecases.createTransaction.TransactionResponse;
import com.SecureBankingApi.domain.transaction.Transaction;
import com.SecureBankingApi.domain.transaction.exceptions.TransactionNotFoundException;
import com.SecureBankingApi.domain.transaction.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetTransactionUseCase {
    private final TransactionRepository transactionRepository;

    public GetTransactionUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionResponse execute(UUID transactionId, UUID requestingUserId, boolean isAdmin){
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));

        boolean isSourceUser = transaction.getSource() != null &&
                transaction.getSource().getUserId().equals(requestingUserId);
        boolean isDestinationUser = transaction.getReceiver() != null &&
                transaction.getReceiver().getUserId().equals(requestingUserId);

        if (!isAdmin && !isSourceUser && !isDestinationUser) {
            throw new RuntimeException("You don't have permission to view this transaction");
        }

        return TransactionResponse.fromDomain(transaction);
    }
}
