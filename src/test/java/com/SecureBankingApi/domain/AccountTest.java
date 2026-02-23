package com.SecureBankingApi.domain;


import com.SecureBankingApi.domain.account.Account;
import com.SecureBankingApi.domain.account.AccountStatus;
import com.SecureBankingApi.domain.account.AccountType;
import com.SecureBankingApi.domain.account.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
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

    @Test
    void ShouldUnblockAccount() {
        Account account = Account.create(
                "0001-00000123",
                "001",
                UUID.randomUUID(),
                AccountType.CHECKING
        );
        account.block();
        account.unblock();

        assertEquals(AccountStatus.ACTIVE, account.getStatus() );
    }

    @Test
    void ShouldCloseAccount(){
        Account account = Account.create(
                "0001-00000123",
                "001",
                UUID.randomUUID(),
                AccountType.CHECKING
        );
        account.close();
        assertEquals(AccountStatus.CLOSED, account.getStatus() );
    }

    @Test
    void ShouldCreditAnAccount() {
        Account account = Account.create(
                "0001-00000123",
                "001",
                UUID.randomUUID(),
                AccountType.CHECKING
        );
        Money money = Money.of(BigDecimal.valueOf(10));
        account.credit(money);

        assertEquals(BigDecimal.valueOf(10), account.getBalance().getValue());
    }

    @Test
    void ShouldDebitFromAccount() {
        Account account = Account.create(
                "0001-00000123",
                "001",
                UUID.randomUUID(),
                AccountType.CHECKING
        );
        Money add = Money.of(BigDecimal.valueOf(10));
        account.credit(add);
        account.debit(Money.of(BigDecimal.valueOf(5)));

        assertEquals(BigDecimal.valueOf(5), account.getBalance().getValue());
    }
}
