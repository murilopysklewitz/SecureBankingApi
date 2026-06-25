package com.SecureBankingApi.application.usecases;

import com.SecureBankingApi.domain.transaction.TransactionCompletedEvent;
import com.SecureBankingApi.infrastructure.messaging.RabbitMQConfiguration;
import com.SecureBankingApi.infrastructure.messaging.RabbitMQTransactionEventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
public class RabbitMQIntegrationTest  {

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3-management-alpine");
    @Autowired
    private RabbitMQTransactionEventPublisher eventPublisher;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void shouldPublishCompletedTransaction() {

        TransactionCompletedEvent event = new TransactionCompletedEvent(
                "email@email.com",
                "email2@email.com",
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
