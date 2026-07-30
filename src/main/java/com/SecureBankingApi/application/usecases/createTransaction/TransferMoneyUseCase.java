package com.SecureBankingApi.application.usecases.createTransaction;

import com.SecureBankingApi.application.exceptions.AssessmentDecisionRejectedException;
import com.SecureBankingApi.domain.account.Account;
import com.SecureBankingApi.domain.account.AccountRepository;
import com.SecureBankingApi.domain.account.exceptions.AccountNotFoundException;
import com.SecureBankingApi.domain.transaction.*;
import com.SecureBankingApi.domain.transaction.exceptions.InvalidTransactionException;
import com.SecureBankingApi.infrastructure.api.webDtos.TransactionCompletedWebDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Service
public class TransferMoneyUseCase {

    private static final Logger log = LoggerFactory.getLogger(TransferMoneyUseCase.class);
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionEventPublisher eventPublisher;

    private final RestClient restClient;

    public TransferMoneyUseCase(TransactionRepository transactionRepository, AccountRepository accountRepository, TransactionEventPublisher eventPublisher, RestClient restClient) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.eventPublisher = eventPublisher;
        this.restClient = restClient;
    }

    @Transactional
    public TransactionResponse execute(TransactionRequest request, UUID userId, String ipAddress){

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

        try{
            RiskAssessmentResponse assessmentResponse = restClient.post()
                    .uri("http://risk-engine:8181/api/risk")
                    .body(new TransactionCompletedWebDTO(transaction.getId(),
                    transaction.getSource().getUserId(),
                    transaction.getReceiver().getUserId(),
                    transaction.getAmount().getValue(),
                    ipAddress))
                    .retrieve()
                    .body(RiskAssessmentResponse.class);
            if (assessmentResponse == null || assessmentResponse.decision() == AssessmentDecision.REJECT) {
                throw new AssessmentDecisionRejectedException(transaction.getId());
            }

            TransactionFlaggedEvent transactionFlaggedEvent = new TransactionFlaggedEvent(transaction.getId(),
                    transaction.getSource().getUserId(),
                    transaction.getReceiver().getUserId(),
                    transaction.getAmount().getValue(),
                    ipAddress);
            eventPublisher.publicTransactionFlagged(transactionFlaggedEvent);
        }catch (RestClientException e){
            log.error("ERROR IN REQUEST URL: http://risk-engine:8181/api/risk");


        }


        source.debit(request.getAmount());
        destination.credit(request.getAmount());


        accountRepository.save(source);
        accountRepository.save(destination);

        transaction.completeTransaction();


        transactionRepository.save(transaction);

        TransactionCompletedEvent event = new TransactionCompletedEvent(
                source.getEmail(),
                destination.getEmail(),
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
