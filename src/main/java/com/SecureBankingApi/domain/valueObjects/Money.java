package com.SecureBankingApi.domain.valueObjects;

import java.math.BigDecimal;

public class Money {
    private BigDecimal value;

    public Money(BigDecimal value){
        if(value.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Negative value");
        }
        this.value = value;
    }

    public Money add(Money other){
        return new Money(this.value.add(other.value));
    }
    public Money subtract(Money other){
        return new Money(this.value.subtract(other.value));
    }

    public BigDecimal getValue() {
        return value;
    }
}
