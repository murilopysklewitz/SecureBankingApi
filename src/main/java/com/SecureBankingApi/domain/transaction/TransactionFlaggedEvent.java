package com.SecureBankingApi.domain.transaction;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionFlaggedEvent(UUID transactionId,
                                      UUID sourceUserId,
                                      UUID destinationUserId,
                                      BigDecimal amount,
                                      String ipAddress) {
}
