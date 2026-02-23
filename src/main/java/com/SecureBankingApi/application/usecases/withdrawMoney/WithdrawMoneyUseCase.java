package com.SecureBankingApi.application.usecases.withdrawMoney;

import com.SecureBankingApi.application.usecases.createTransaction.TransactionResponse;
import com.SecureBankingApi.domain.account.Account;
import com.SecureBankingApi.domain.account.AccountRepository;
import com.SecureBankingApi.domain.account.Money;
import com.SecureBankingApi.domain.account.exceptions.AccountNotFoundException;
import com.SecureBankingApi.domain.account.exceptions.InsufficientBalanceException;
import com.SecureBankingApi.domain.transaction.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WithdrawMoneyUseCase {
    private static final Logger log = LoggerFactory.getLogger(WithdrawMoneyUseCase.class);

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public WithdrawMoneyUseCase(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public TransactionResponse execute(WithdrawMoneyRequest request, UUID requestingUserId) {

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException(request.getAccountId()));

        if (!account.getUserId().equals(requestingUserId)) {
            throw new InvalidTransactionException("You can only withdraw from your own accounts");
        }

        Money amount = Money.of(request.getAmount());

        if (!account.hasSufficientBalance(amount)) {
            throw new InvalidTransactionException("insufficient balance");
        }

        AccountDataTransaction sourceInfo = AccountDataTransaction.of(
                account.getUserId(),
                account.getAccountNumber(),
                account.getAgency()
        );

        Transaction transaction = Transaction.create(
                sourceInfo,
                null,
                TransactionType.WITHDRAWAL,
                amount,
                request.getDescription()
        );

            account.debit(amount);

            accountRepository.save(account);

            transaction.completeTransaction();
            transactionRepository.save(transaction);

            return TransactionResponse.fromDomain(transaction);

    }
}