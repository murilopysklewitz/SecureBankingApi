package com.SecureBankingApi.application.usecases.createAccount;

import com.SecureBankingApi.domain.account.Account;
import com.SecureBankingApi.domain.account.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateAccountUseCase {
    private final AccountRepository repository;

    public CreateAccountUseCase(AccountRepository repository) {
        this.repository = repository;
    }

    public AccountResponse execute(CreateAccountRequest request){
        String accountNumber = UUID.randomUUID().toString();
        Account account = Account.create(accountNumber,
                "001", request.getUserId(),
                request.getType());

        repository.save(account);

        return new AccountResponse(account.getId(),
                account.getAccountNumber(),
                account.getAgency(),
                account.getUserId(),
                account.getBalance(),
                account.getType(),
                account.getStatus(),
                account.getCreatedAt());
    }

}
