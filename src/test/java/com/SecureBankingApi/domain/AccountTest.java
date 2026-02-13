package com.SecureBankingApi.domain;


import com.SecureBankingApi.domain.account.Account;
import com.SecureBankingApi.domain.account.AccountStatus;
import com.SecureBankingApi.domain.account.AccountType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {
    @Test
    void ShouldCreateAnAccountSuccessfully(){
        UUID userId = UUID.randomUUID();
        String accountNumber = "0001-00000123";
        String agency = "0001";
        AccountType type = AccountType.CHECKING;


        Account account = Account.create(
                accountNumber,
                agency,
                userId,
                type
        );

        assertNotNull(account);
        assertNull(account.getId());
        assertEquals(userId, account.getUserId());
        assertEquals(accountNumber, account.getAccountNumber());
        assertEquals(agency, account.getAgency());
        assertEquals(type, account.getType());
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
        assertTrue(account.getBalance().isZero());
        assertNotNull(account.getCreatedAt());
        assertNotNull(account.getUpdatedAt());

    }

    @Test
    void ShouldCreateCheckingAccount() {
        Account account = Account.create(
                "0001-00000123",
                "001",
                UUID.randomUUID(),
                AccountType.CHECKING
        );
        assertEquals(AccountType.CHECKING, account.getType());
    }

    @Test
    void ShouldCreateSavingsAccount() {
        Account account = Account.create(
                "001-00000123",
                "001",
                UUID.randomUUID(),
                AccountType.SAVINGS
        );

        assertEquals(AccountType.SAVINGS, account.getType());
    }

    @Test
    void ShouldBlockAccount() {
        Account account = Account.create(
                "0001-00000123",
                "001",
                UUID.randomUUID(),
                AccountType.CHECKING
        );

        account.block();

        assertEquals(account.getStatus(), AccountStatus.BLOCKED);
    }

}
