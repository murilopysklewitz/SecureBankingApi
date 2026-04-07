package com.SecureBankingApi.domain.transaction;

public interface TransactionEventPublisher {
    void publishTransactionCompleted(TransactionCompletedEvent event);
}
