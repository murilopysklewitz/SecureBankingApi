package com.SecureBankingApi.infrastructure.api.webDtos;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionCompletedWebDTO(@NotNull UUID transactionId,
                                         @NotNull UUID sourceUserId,
                                         @NotNull UUID destinationUserId,
                                         @NotNull BigDecimal amount,
                                         @NotNull String ipAddress) { }