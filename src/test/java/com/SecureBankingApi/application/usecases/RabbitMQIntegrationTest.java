package com.SecureBankingApi.application.usecases;

import com.SecureBankingApi.IntegrationTestBase;
import com.SecureBankingApi.domain.transaction.TransactionCompletedEvent;
import com.SecureBankingApi.domain.transaction.TransactionEventPublisher;
import com.SecureBankingApi.infrastructure.messaging.RabbitMQConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RabbitMQIntegrationTest extends IntegrationTestBase {

    @Autowired
    private TransactionEventPublisher eventPublisher;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void shouldPublishCompletedTransaction() {

        TransactionCompletedEvent event = new TransactionCompletedEvent(
                java.util.UUID.randomUUID(),
                java.util.UUID.randomUUID(),
                java.util.UUID.randomUUID(),
                new java.math.BigDecimal("100.00"),
                "TRANSFER",
                LocalDateTime.now()
        );

        eventPublisher.publishTransactionCompleted(event);

        TransactionCompletedEvent completedEvent  = (TransactionCompletedEvent) rabbitTemplate.receiveAndConvert(RabbitMQConfiguration.TRANSACTION_QUEUE,
                5000);
        assertNotNull(completedEvent);
        assertEquals(event.getTransactionId(), completedEvent.getTransactionId());


    }
}
