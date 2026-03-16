package com.SecureBankingApi.domain.transaction;

import com.SecureBankingApi.domain.account.AccountNumber;
import com.SecureBankingApi.domain.transaction.exceptions.InvalidAccountData;

import java.util.UUID;

public class AccountDataTransaction {
    private final UUID userId;
    private final UUID accountId;
    private final AccountNumber accountNumber;
    private final String agency;

    private AccountDataTransaction(UUID userId, UUID accountId, AccountNumber accountNumber, String agency) {
        this.userId = userId;
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.agency = agency;
    }

    public static AccountDataTransaction of(UUID userId, UUID accountId, AccountNumber accountNumber, String agency){
        if(userId == null ){
            throw new InvalidAccountData("user id cannot be null");
        }
        if(accountId == null){
            throw new InvalidAccountData("account id cannot be null");
        }
        if(accountNumber == null){
            throw new InvalidAccountData("account number cannot be null");
        }
        if(agency == null || agency.isBlank() ){
            throw new InvalidAccountData("agency cannot be null");
        }

        return new AccountDataTransaction(userId, accountId, accountNumber, agency);
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public AccountNumber getAccountNumber() {
        return accountNumber;
    }

    public String getAgency() {
        return agency;
    }
}
