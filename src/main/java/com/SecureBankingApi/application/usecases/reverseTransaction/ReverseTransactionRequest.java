package com.SecureBankingApi.application.usecases.reverseTransaction;

import java.util.UUID;

public record ReverseTransactionRequest(UUID transactionId, String reason) {
}
