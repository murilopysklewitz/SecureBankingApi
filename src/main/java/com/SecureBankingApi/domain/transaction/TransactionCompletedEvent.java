package com.SecureBankingApi.domain.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionCompletedEvent {
        private final UUID transactionId;
        private final UUID sourceUserId;
        private final UUID destinationUserId;
        private final BigDecimal amount;
        private final String type;
        private final LocalDateTime completedAt;

        public TransactionCompletedEvent(
                UUID transactionId,
                UUID sourceUserId,
                UUID destinationUserId,
                BigDecimal amount,
                String type,
                LocalDateTime completedAt) {
            this.transactionId = transactionId;
            this.sourceUserId = sourceUserId;
            this.destinationUserId = destinationUserId;
            this.amount = amount;
            this.type = type;
            this.completedAt = completedAt;
        }

        public UUID getTransactionId() { return transactionId; }
        public UUID getSourceUserId() { return sourceUserId; }
        public UUID getDestinationUserId() { return destinationUserId; }
        public BigDecimal getAmount() { return amount; }
        public String getType() { return type; }
        public LocalDateTime getCompletedAt() { return completedAt; }
}
