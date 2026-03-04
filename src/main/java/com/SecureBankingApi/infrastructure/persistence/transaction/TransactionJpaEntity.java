package com.SecureBankingApi.infrastructure.persistence.transaction;

import com.SecureBankingApi.domain.transaction.TransactionStatus;
import com.SecureBankingApi.domain.transaction.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "transactions",
        indexes = {
                @Index(name = "idx_transactions_source_user_id", columnList = "source_user_id"),
                @Index(name = "idx_transactions_destination_user_id", columnList = "destination_user_id"),
                @Index(name = "idx_transactions_status", columnList = "status"),
                @Index(name = "idx_transactions_type", columnList = "type"),
                @Index(name = "idx_transactions_created_at", columnList = "created_at"),
        }
)
public class TransactionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, updatable = false)
    private UUID id;

    @Column(name = "source_user_id")
    private UUID sourceUserId;

    @Column(name = "source_account_id")
    private UUID sourceAccountId;

    @Column(name = "source_account_number")
    private String sourceAccountNumber;

    @Column(name = "source_agency")
    private String sourceAgency;

    @Column(name = "destination_user_id")
    private UUID destinationUserId;

    @Column(name = "destination_account_id")
    private UUID destinationAccountId;


    @Column(name = "destination_account_number")
    private String destinationAccountNumber;

    @Column(name = "destination_agency")
    private String destinationAgency;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TransactionStatus status;

    @Column(name = "type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private  TransactionType type;

    @Column(name = "description")
    private String description;
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "completed_at")
    private LocalDateTime completedAt;


    public TransactionJpaEntity(UUID id,
                                UUID sourceUserId,
                                UUID sourceAccountId,
                                String sourceAccountNumber,
                                String sourceAgency,
                                UUID destinationUserId,
                                UUID destinationAccountId,
                                String destinationAccountNumber,
                                String destinationAgency,
                                TransactionStatus status,
                                TransactionType type,
                                String description,
                                BigDecimal amount,
                                LocalDateTime createdAt,
                                LocalDateTime completedAt) {
        this.id = id;
        this.sourceUserId = sourceUserId;
        this.sourceAccountId = sourceAccountId;
        this.sourceAccountNumber = sourceAccountNumber;
        this.sourceAgency = sourceAgency;
        this.destinationUserId = destinationUserId;
        this.destinationAccountId = destinationAccountId;
        this.destinationAccountNumber = destinationAccountNumber;
        this.destinationAgency = destinationAgency;
        this.status = status;
        this.type = type;
        this.description = description;
        this.amount = amount;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

    public TransactionJpaEntity() {
    }

    public UUID getDestinationAccountId() {
        return destinationAccountId;
    }

    public void setDestinationAccountId(UUID destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }

    public UUID getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(UUID sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSourceUserId() {
        return sourceUserId;
    }

    public void setSourceUserId(UUID sourceUserId) {
        this.sourceUserId = sourceUserId;
    }

    public String getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    public void setSourceAccountNumber(String sourceAccountNumber) {
        this.sourceAccountNumber = sourceAccountNumber;
    }

    public String getSourceAgency() {
        return sourceAgency;
    }

    public void setSourceAgency(String sourceAgency) {
        this.sourceAgency = sourceAgency;
    }

    public UUID getDestinationUserId() {
        return destinationUserId;
    }

    public void setDestinationUserId(UUID destinationUserId) {
        this.destinationUserId = destinationUserId;
    }

    public String getDestinationAccountNumber() {
        return destinationAccountNumber;
    }

    public void setDestinationAccountNumber(String destinationAccountNumber) {
        this.destinationAccountNumber = destinationAccountNumber;
    }

    public String getDestinationAgency() {
        return destinationAgency;
    }

    public void setDestinationAgency(String destinationAgency) {
        this.destinationAgency = destinationAgency;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
