package com.SecureBankingApi.domain;

import com.SecureBankingApi.domain.account.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void ShouldCreateAZeroValueMoney(){
        Money money1 = Money.zero();

        assertEquals(BigDecimal.ZERO, money1.getValue());
    }
    @Test
    void ShouldAddAmount(){
        Money result = money.add(money2);

        assertEquals(BigDecimal.valueOf(15), result.getValue());
    }

    @Test
    void ShouldSubtractAmount() {
        Money result = money.subtract(money2);

        assertEquals(BigDecimal.valueOf(5), result.getValue());
    }
}
