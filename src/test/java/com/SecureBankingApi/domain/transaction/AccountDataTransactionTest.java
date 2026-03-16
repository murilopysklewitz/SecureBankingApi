package com.SecureBankingApi.domain.transaction;

import com.SecureBankingApi.domain.account.AccountNumber;
import com.SecureBankingApi.domain.transaction.exceptions.InvalidAccountData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AccountDataTransactionTest {
    private UUID userId;
    private AccountNumber accountNumber;
    private String agency;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        accountNumber = AccountNumber.generate();
        agency ="001";
    }

    @Test
    void ShouldCreateAnAccountDataTransaction() {
        AccountDataTransaction dataTransaction = AccountDataTransaction.of(
                userId,
                UUID.randomUUID(),
                accountNumber,
                agency
        );

        assertNotNull(dataTransaction.getUserId());
        assertEquals(accountNumber, dataTransaction.getAccountNumber());
        assertEquals("001", dataTransaction.getAgency());
    }

    @Test
    void ShouldThrowExceptionWhenUserIdIsNull() {
        assertThrows(InvalidAccountData.class, () ->{
            AccountDataTransaction.of(
                    null,
                    UUID.randomUUID(),
                    accountNumber,
                    agency
            );
        });
    }

    @Test
    void ShouldThrowExceptionWhenAccountNumberIsNull() {
        assertThrows(InvalidAccountData.class, () ->{
            AccountDataTransaction.of(
                    userId,
                    UUID.randomUUID(),
                    null,
                    agency
            );
        });
    }

    @Test
    void ShouldThrowExceptionWhenAgencyIsNull() {
        assertThrows(InvalidAccountData.class, () ->{
            AccountDataTransaction.of(
                    userId,
                    UUID.randomUUID(),
                    accountNumber,
                    null
            );
        });
    }

}
