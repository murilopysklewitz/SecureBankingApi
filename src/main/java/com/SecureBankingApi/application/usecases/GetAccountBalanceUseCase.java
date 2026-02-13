package com.SecureBankingApi.application.usecases;

import com.SecureBankingApi.application.usecases.createAccount.AccountResponse;
import com.SecureBankingApi.domain.account.Account;
import com.SecureBankingApi.domain.account.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
@Service
public class GetAccountBalanceUseCase {

    private final AccountRepository accountRepository;

    public GetAccountBalanceUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountBalanceResponse execute(UUID userId, UUID accountId, boolean isAdmin){

        Account account = accountRepository
                .findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account no found"));

        if(!isAdmin && account.getUserId() != userId){
            throw new RuntimeException("unauthorized access");
        };

        return new AccountBalanceResponse(account.getId(),
                account.getAccountNumber(),
                account.getBalance().getValue(),
                account.getStatus());
    }
}
