package com.SecureBankingApi.application.usecases;

import com.SecureBankingApi.application.usecases.createAccount.AccountResponse;
import com.SecureBankingApi.domain.account.Account;
import com.SecureBankingApi.domain.account.AccountRepository;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.UUID;

@Service
public class GetAccountDetailsUseCase {

    private final AccountRepository accountRepository;

    public GetAccountDetailsUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    public AccountResponse execute(UUID accountId, UUID requestingUserId, boolean isAdmin) {

        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found"));
        if (!isAdmin && account.getUserId() != requestingUserId) {
            throw new RuntimeException(
                    "You don't have permission to view this account"
            );
        }

        return AccountResponse.fromDomain(account);
    }
}