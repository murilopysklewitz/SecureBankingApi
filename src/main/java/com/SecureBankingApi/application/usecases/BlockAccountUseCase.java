package com.SecureBankingApi.application.usecases;

import com.SecureBankingApi.domain.account.Account;
import com.SecureBankingApi.domain.account.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class BlockAccountUseCase {

    private final AccountRepository accountRepository;

    public BlockAccountUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    @Transactional
    public void execute(UUID accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.block();

        accountRepository.save(account);

    }
}
