package com.SecureBankingApi.domain.transaction;

import java.util.UUID;

public class AccountDataTransaction {
    private UUID userId;
    private String accountNumber;
    private String agency;

    private AccountDataTransaction(UUID userId, String accountNumber, String agency) {
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.agency = agency;
    }

    public static AccountDataTransaction of(UUID userId, String accountNumber, String agency){
        if(userId == null ){
            throw new RuntimeException("user id cannot be null");
        }
        if(accountNumber == null || accountNumber.isBlank()){
            throw new RuntimeException("account number cannot be null");
        }
        if(agency == null || agency.isBlank() ){
            throw new RuntimeException("agency cannot be null");
        }

        return new AccountDataTransaction(userId, accountNumber, agency);
    }

    public UUID getUserId() {
        return userId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAgency() {
        return agency;
    }
}
