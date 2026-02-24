package com.SecureBankingApi.application.config;

import com.SecureBankingApi.application.usecases.depositMoney.DepositMoneyUseCase;
import com.SecureBankingApi.domain.account.AccountRepository;
import com.SecureBankingApi.domain.transaction.TransactionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DepositMoneyUseCaseConfig {
    @Bean
            public DepositMoneyUseCase depositMoneyUseCase(
                    AccountRepository accountRepository,
                    TransactionRepository transactionRepository
    ){
        return new DepositMoneyUseCase(
                accountRepository, transactionRepository);
    }
}
