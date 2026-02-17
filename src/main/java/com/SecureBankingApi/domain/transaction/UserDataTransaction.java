package com.SecureBankingApi.domain.transaction;

import java.util.UUID;

public class UserDataTransaction {
    private final UUID userId;
    private final String accountNumber;
    private final String agency;

    private UserDataTransaction(UUID userId, String accountNumber, String agency) {
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.agency = agency;
    }

    public static UserDataTransaction of(UUID userId, String accountNumber, String agency){
        if(userId == null ){
            throw new RuntimeException("user id cannot be null");
        }
        if(accountNumber == null || accountNumber.isBlank()){
            throw new RuntimeException("account number cannot be null");
        }
        if(agency == null || agency.isBlank() ){
            throw new RuntimeException("agency cannot be null");
        }

        return new UserDataTransaction(userId, accountNumber, agency);
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
