package com.SecureBankingApi.application.config;

import com.SecureBankingApi.application.usecases.createTransaction.TransferMoneyUseCase;
import com.SecureBankingApi.domain.account.AccountRepository;
import com.SecureBankingApi.domain.transaction.TransactionEventPublisher;
import com.SecureBankingApi.domain.transaction.TransactionRepository;
import com.SecureBankingApi.domain.user.ports.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransferMoneyUseCaseConfig {

    @Bean
    public TransferMoneyUseCase transferMoneyUseCase(AccountRepository accountRepository,
                                                     UserRepository userRepository,
                                                     TransactionRepository transactionRepository, TransactionEventPublisher eventPublisher){
        return new TransferMoneyUseCase(transactionRepository, accountRepository, userRepository, eventPublisher);
    }
}
