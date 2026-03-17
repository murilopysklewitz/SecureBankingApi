package com.SecureBankingApi.application.usecases.getTransactionHistory;

import com.SecureBankingApi.application.usecases.createTransaction.TransactionResponse;
import com.SecureBankingApi.domain.PageRequest;
import com.SecureBankingApi.domain.PageResult;
import com.SecureBankingApi.domain.account.Account;
import com.SecureBankingApi.domain.account.AccountRepository;
import com.SecureBankingApi.domain.account.exceptions.AccountNotFoundException;
import com.SecureBankingApi.domain.transaction.Transaction;
import com.SecureBankingApi.domain.transaction.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetTransactionHistoryUseCase {
    private static final Logger log = LoggerFactory.getLogger(GetTransactionHistoryUseCase.class);

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public GetTransactionHistoryUseCase(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public PageResult<TransactionResponse> execute(UUID accountId, UUID requestingUserId, boolean isAdmin, PageRequest pageRequest) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        if (!isAdmin && !account.getUserId().equals(requestingUserId)) {
            throw new RuntimeException("You don't have permission to view this account's transactions");
        }

        PageResult<Transaction> page = transactionRepository.findByAccountIdPage(accountId, pageRequest);

        List<TransactionResponse> content = page.getContent().stream().map((t) -> TransactionResponse.fromDomain(t)).toList();

        return new PageResult<>(
                page.getPage(),
                page.getSize(),
                page.getTotalElements(),
                content
        );
    }
}
