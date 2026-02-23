package com.SecureBankingApi.application.usecases.getAccount;

import com.SecureBankingApi.application.usecases.createAccount.AccountResponse;
import com.SecureBankingApi.domain.account.Account;
import com.SecureBankingApi.domain.account.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListMyAccountsUseCase {

    private final AccountRepository accountRepository;

    public ListMyAccountsUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<AccountResponse> execute(UUID userId) {

        List<Account> accounts = accountRepository.findByUserId(userId);

        return accounts.stream()
                .map(AccountResponse::fromDomain)
                .toList();
    }
}
