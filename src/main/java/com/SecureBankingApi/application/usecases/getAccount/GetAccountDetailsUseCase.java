package com.SecureBankingApi.application.usecases.getAccount;

import com.SecureBankingApi.application.exceptions.PermissionDeniedException;
import com.SecureBankingApi.application.usecases.createAccount.AccountResponse;
import com.SecureBankingApi.domain.account.Account;
import com.SecureBankingApi.domain.account.AccountRepository;
import com.SecureBankingApi.domain.account.exceptions.AccountNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetAccountDetailsUseCase {

    private final AccountRepository accountRepository;

    public GetAccountDetailsUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    public AccountResponse execute(UUID accountId, UUID requestingUserId, boolean isAdmin) {

        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));
        if (!isAdmin && !account.getUserId().equals(requestingUserId)) {
            throw new PermissionDeniedException(
                    "You don't have permission to view this account"
            );
        }

        return AccountResponse.fromDomain(account);
    }
}