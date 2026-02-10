package com.SecureBankingApi.application.usecases;

import com.SecureBankingApi.domain.account.Account;
import com.SecureBankingApi.domain.account.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CloseAccountUseCase {

    private final AccountRepository accountRepository;

    public CloseAccountUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void execute(UUID accountId, UUID userId, boolean isAdmin) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!isAdmin && account.getUserId() != userId) {
            throw new RuntimeException(
                    "You don't have permission to close this account"
            );
        }

        account.close();


    }
}
