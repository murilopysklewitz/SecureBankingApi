package com.SecureBankingApi.infrastructure.messaging;

import com.SecureBankingApi.domain.transaction.TransactionCompletedEvent;
import com.SecureBankingApi.domain.transaction.TransactionEventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQTransactionEventPublisher implements TransactionEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQTransactionEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishTransactionCompleted(TransactionCompletedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.TRANSACTION_EXCHANGE,
                RabbitMQConfiguration.TRANSACTION_COMPLETED_ROUTING_KEY,
                event
        );
    }
}
