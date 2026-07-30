package com.SecureBankingApi.application.exceptions;

import java.util.UUID;

public class AssessmentDecisionRejectedException extends RuntimeException {
    public AssessmentDecisionRejectedException(UUID transactionId) {
        super("transaction with Id:" + transactionId + "has been rejected");
    }
}
