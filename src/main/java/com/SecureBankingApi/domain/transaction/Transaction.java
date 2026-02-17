package com.SecureBankingApi.domain.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private UUID id;
    private UserDataTransaction receiver;
    private UserDataTransaction source;


    private TransactionStatus status;
    private TransactionType type;

    private String description;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}