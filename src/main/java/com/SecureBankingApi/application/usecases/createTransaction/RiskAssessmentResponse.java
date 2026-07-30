package com.SecureBankingApi.application.usecases.createTransaction;

import java.util.List;

public record RiskAssessmentResponse(int score, AssessmentDecision decision, List<String> rules) {
}