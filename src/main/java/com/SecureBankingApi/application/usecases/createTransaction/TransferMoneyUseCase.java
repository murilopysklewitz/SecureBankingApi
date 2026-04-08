package com.SecureBankingApi.application.usecases.createTransaction;

import com.SecureBankingApi.application.exceptions.UserNotFoundException;
import com.SecureBankingApi.domain.account.Account;
import com.SecureBankingApi.domain.account.AccountRepository;
import com.SecureBankingApi.domain.account.exceptions.AccountNotFoundException;
import com.SecureBankingApi.domain.transaction.*;
import com.SecureBankingApi.domain.transaction.exceptions.InvalidTransactionException;
import com.SecureBankingApi.domain.user.User;
import com.SecureBankingApi.domain.user.ports.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class TransferMoneyUseCase {

    private static final Logger log = LoggerFactory.getLogger(TransferMoneyUseCase.class);
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionEventPublisher eventPublisher;

    public TransferMoneyUseCase(TransactionRepository transactionRepository, AccountRepository accountRepository, UserRepository userRepository, TransactionEventPublisher eventPublisher) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public TransactionResponse execute(TransactionRequest request, UUID userId){
        Account source = accountRepository.findById(request.getSourceAccountId()).orElseThrow(() -> new AccountNotFoundException(request.getSourceAccountId()));
        if(!source.getUserId().equals(userId)){
            throw new IllegalCallerException("user Id mismatch");
        }
        source.ensureActive();

        Account destination = accountRepository.findById(request.getDestinationAccountId()).orElseThrow(() -> new AccountNotFoundException(request.getDestinationAccountId()));
        destination.ensureActive();

        if(destination.getUserId().equals(source.getUserId())){
            throw new InvalidTransactionException("cannot make transactions to yourself");
        }
        if(!source.hasSufficientBalance(request.getAmount())){
            throw new InvalidTransactionException("source has no sufficient balance to debit");
        }

        Optional<User> userSource = userRepository.findById(source.getUserId());
        Optional<User> userDestination = userRepository.findById(destination.getUserId());

        AccountDataTransaction sourceInfo = AccountDataTransaction.of(
                source.getUserId(),
                source.getId(),
                source.getAccountNumber(),
                source.getAgency()
        );

        AccountDataTransaction destinationInfo = AccountDataTransaction.of(
                destination.getUserId(),
                destination.getId(),
                destination.getAccountNumber(),
                destination.getAgency()
        );

        Transaction transaction = Transaction.create(
                sourceInfo,
                destinationInfo,
                TransactionType.TRANSFER,
                request.getAmount(),
                null
        );
        source.debit(request.getAmount());
        destination.credit(request.getAmount());


        accountRepository.save(source);
        accountRepository.save(destination);

        transaction.completeTransaction();


        transactionRepository.save(transaction);

        TransactionCompletedEvent event = new TransactionCompletedEvent(
                userSource.stream().map((u) -> u.getEmail()).toString(),
                userDestination.stream().map((u) -> u.getEmail()).toString(),
                transaction.getId(),
                source.getId(),
                destination.getId(),
                transaction.getAmount().getValue(),
                transaction.getType().toString(),
                transaction.getCompletedAt()
        );
        eventPublisher.publishTransactionCompleted(event);
        return TransactionResponse.fromDomain(transaction);

    }
}
