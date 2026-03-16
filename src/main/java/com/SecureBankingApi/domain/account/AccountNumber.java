package com.SecureBankingApi.domain.account;

import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;

public class AccountNumber {
    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("\\d{6}-\\d");
    private final String accountNumber;

    private AccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public static AccountNumber generate() {
        Random random = new Random();
        int firstPart = random.nextInt(1000000);
        int secondPart = random.nextInt(10); 
        String generated = String.format("%06d-%d", firstPart, secondPart);
        return new AccountNumber(generated);
    }

    public static AccountNumber restore(String accountNumber) {
        if (accountNumber == null || !ACCOUNT_NUMBER_PATTERN.matcher(accountNumber).matches()) {
            throw new IllegalArgumentException("Invalid account number format: " + accountNumber);
        }
        return new AccountNumber(accountNumber);
    }

    public String getValue() {
        return accountNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountNumber that = (AccountNumber) o;
        return Objects.equals(accountNumber, that.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    @Override
    public String toString() {
        return accountNumber;
    }
}
