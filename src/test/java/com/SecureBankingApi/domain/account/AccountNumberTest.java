package com.SecureBankingApi.domain.account;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.regex.Pattern;

public class AccountNumberTest {

    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("\\d{6}-\\d");

    @Test
    public void testGenerate() {
        AccountNumber accountNumber = AccountNumber.generate();
        assertNotNull(accountNumber);
        String value = accountNumber.getValue();
        assertTrue(ACCOUNT_NUMBER_PATTERN.matcher(value).matches());
    }

    @Test
    public void testRestoreValid() {
        String validNumber = "123456-7";
        AccountNumber accountNumber = AccountNumber.restore(validNumber);
        assertNotNull(accountNumber);
        assertEquals(validNumber, accountNumber.getValue());
    }

    @Test
    public void testRestoreInvalidNull() {
        assertThrows(IllegalArgumentException.class, () -> AccountNumber.restore(null));
    }

    @Test
    public void testRestoreInvalidEmpty() {
        assertThrows(IllegalArgumentException.class, () -> AccountNumber.restore(""));
    }

    @Test
    public void testRestoreInvalidWrongFormat() {
        assertThrows(IllegalArgumentException.class, () -> AccountNumber.restore("12345-6"));
        assertThrows(IllegalArgumentException.class, () -> AccountNumber.restore("1234567"));
        assertThrows(IllegalArgumentException.class, () -> AccountNumber.restore("123456-78"));
        assertThrows(IllegalArgumentException.class, () -> AccountNumber.restore("abc123-4"));
    }

    @Test
    public void testGetValue() {
        String number = "654321-0";
        AccountNumber accountNumber = AccountNumber.restore(number);
        assertEquals(number, accountNumber.getValue());
    }

    @Test
    public void testEquals() {
        AccountNumber a1 = AccountNumber.restore("111111-1");
        AccountNumber a2 = AccountNumber.restore("111111-1");
        AccountNumber a3 = AccountNumber.restore("222222-2");

        assertEquals(a1, a2);
        assertNotEquals(a1, a3);
        assertNotEquals(a1, null);
        assertNotEquals(a1, "not an account number");
    }

    @Test
    public void testHashCode() {
        AccountNumber a1 = AccountNumber.restore("333333-3");
        AccountNumber a2 = AccountNumber.restore("333333-3");
        AccountNumber a3 = AccountNumber.restore("444444-4");

        assertEquals(a1.hashCode(), a2.hashCode());
        assertNotEquals(a1.hashCode(), a3.hashCode());
    }

    @Test
    public void testToString() {
        String number = "555555-5";
        AccountNumber accountNumber = AccountNumber.restore(number);
        assertEquals(number, accountNumber.toString());
    }
}
