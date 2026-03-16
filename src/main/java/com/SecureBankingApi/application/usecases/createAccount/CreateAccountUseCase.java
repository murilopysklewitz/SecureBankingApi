package com.SecureBankingApi.application.usecases.createAccount;

import com.SecureBankingApi.application.exceptions.BusinessException;
import com.SecureBankingApi.domain.account.Account;
import com.SecureBankingApi.domain.account.AccountNumber;
import com.SecureBankingApi.domain.account.AccountRepository;
import jakarta.transaction.Transactional;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateAccountUseCase {
    private static final Logger log = LoggerFactory.getLogger(CreateAccountUseCase.class);
    private final AccountRepository repository;

    public CreateAccountUseCase(AccountRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AccountResponse execute(CreateAccountRequest request){
        log.info("[CREATE ACCOUNT USECASE] STARTING TO SAVE ACCOUNT OF {}", request.getUserId());

        if(repository.existsByUserIdAndType(request.getUserId(), request.getType())){
            log.error("[CREATE ACCOUNT USECASE] ALREADY HAVE AN ACCOUNT WITH USER {} AND TYPE {}", request.getUserId(), request.getType());
            throw new BusinessException("Already have an account with same time for this user");
        }
        String accountNumber = UUID.randomUUID().toString();
        Account account = Account.create(AccountNumber.generate(),
                "001", request.getUserId(),
                request.getType());


        try {
            repository.save(account);
            log.debug("[CREATE ACCOUNT USECASE] ACCOUNT OF USER ID {} SAVED SUCCESSFULLY", account.getUserId());
        } catch (Exception e) {
            log.error("[CREATE ACCOUNT USECASE] FAILED TO SAVE ACCOUNT FROM USER ID {}", account.getUserId());
            throw new RuntimeException(e);
        }
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
