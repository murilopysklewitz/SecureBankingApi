package com.SecureBankingApi.application.usecases.depositMoney;

import com.SecureBankingApi.application.usecases.createTransaction.TransactionResponse;
import com.SecureBankingApi.domain.account.Account;
import com.SecureBankingApi.domain.account.AccountRepository;
import com.SecureBankingApi.domain.account.Money;
import com.SecureBankingApi.domain.account.exceptions.AccountNotFoundException;
import com.SecureBankingApi.domain.transaction.*;
import com.SecureBankingApi.domain.transaction.exceptions.InvalidTransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DepositMoneyUseCase{
    private static final Logger log = LoggerFactory.getLogger(DepositMoneyUseCase.class);

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public DepositMoneyUseCase(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public TransactionResponse execute(DepositMoneyRequest request, UUID requestingUserId) {

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException(request.getAccountId()));

        if (!account.getUserId().equals(requestingUserId)) {
            throw new InvalidTransactionException("You can only deposit to your own accounts");
        }

        account.ensureActive();

        Money amount = Money.of(request.getAmount());

        AccountDataTransaction destinationInfo = AccountDataTransaction.of(
                account.getUserId(),
                account.getId(),
                account.getAccountNumber(),
                account.getAgency()
        );

        Transaction transaction = Transaction.create(
                destinationInfo,
                null,
                TransactionType.DEPOSIT,
                amount,
                request.getDescription()
        );

        try {
            account.credit(amount);

            accountRepository.save(account);

            transaction.completeTransaction();
            transactionRepository.save(transaction);


            return TransactionResponse.fromDomain(transaction);

        } catch (Exception e) {
            transaction.failTransaction();
            transactionRepository.save(transaction);
            throw new InvalidTransactionException("Deposit failed: " + e.getMessage());
        }
    }
}
