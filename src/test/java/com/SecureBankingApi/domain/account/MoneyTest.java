package com.SecureBankingApi.domain.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

public class MoneyTest {
    private Money money;
    private Money money2;

    @BeforeEach
     void CreateMoney() {
        money =  Money.of(BigDecimal.valueOf(10));
        money2 =  Money.of(BigDecimal.valueOf(5));
    }
    @Test
    void ShouldCreateAValidMoney() {
        Money money1 = Money.of(BigDecimal.valueOf(2));
        assertEquals(BigDecimal.valueOf(2), money1.getValue());
    }

    @Test
    void ShouldCreateAZeroValueMoney() {
        Money money1 = Money.zero();

        assertEquals(BigDecimal.ZERO, money1.getValue());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNegative() {

        BigDecimal negativeValue = BigDecimal.valueOf(-50.00);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Money.of(negativeValue)
        );

        assertEquals("Negative value", exception.getMessage());
    }
}


@Nested
class ArithmeticOperationsTests {

    @Test
    void shouldAddTwoValuesCorrectly() {
        Money money1 = Money.of(BigDecimal.valueOf(100.00));
        Money money2 = Money.of(BigDecimal.valueOf(50.00));

        Money result = money1.add(money2);

        assertEquals(BigDecimal.valueOf(150.00), result.getValue());

        assertEquals(BigDecimal.valueOf(100.00), money1.getValue(),
                "Money1 ");
        assertEquals(BigDecimal.valueOf(50.00), money2.getValue(),
                "Money2 ");
    }

    @Test
    void shouldSubtractWhenSufficientBalance() {
        Money money1 = Money.of(BigDecimal.valueOf(100.00));
        Money money2 = Money.of(BigDecimal.valueOf(30.00));

        Money result = money1.subtract(money2);
        assertEquals(BigDecimal.valueOf(70.00), result.getValue());
    }


    @Test
    void shouldThrowExceptionWhenSubtractingLargerValue() {

        Money money1 = Money.of(BigDecimal.valueOf(50.00));
        Money money2 = Money.of(BigDecimal.valueOf(100.00));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> money1.subtract(money2)
        );

        assertEquals("Insufficient balance", exception.getMessage());
    }
}

@Nested
class ComparisonTests {

    @Test
    void shouldIdentifyWhenIsZero() {
        Money zero1 = Money.zero();
        Money zero2 = Money.of(BigDecimal.ZERO);
        Money nonZero = Money.of(BigDecimal.ONE);

        assertTrue(zero1.isZero());
        assertTrue(zero2.isZero());
        assertFalse(nonZero.isZero());
    }

}

@Nested
class EdgeCaseTests {

    @Test
    void shouldHandleSmallValues() {
        Money money = Money.of(BigDecimal.valueOf(0.01));

        assertNotNull(money);
        assertEquals(BigDecimal.valueOf(0.01), money.getValue());
    }

    @Test
    void shouldHandleLargeValues() {
        BigDecimal largeValue = new BigDecimal("999999999999.99");
        Money money = Money.of(largeValue);

        assertNotNull(money);
        assertEquals(largeValue, money.getValue());
    }

    @Test
    void shouldPreservePrecisionInOperations() {

        Money money1 = Money.of(new BigDecimal("0.1"));
        Money money2 = Money.of(new BigDecimal("0.2"));

        Money result = money1.add(money2);

        assertEquals(new BigDecimal("0.3"), result.getValue());
    }
}
