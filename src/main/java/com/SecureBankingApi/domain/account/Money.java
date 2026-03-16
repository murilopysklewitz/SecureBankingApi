package com.SecureBankingApi.domain.account;

import java.math.BigDecimal;

public class Money {
    private BigDecimal value;

    private Money(BigDecimal value){
        if(value.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Negative value");
        }
        this.value = value;
    }

    public static Money zero(){
        return new Money(BigDecimal.ZERO);
    }

    public static Money of(BigDecimal value){
        return new Money(value);
    }

    public Money add(Money other) {
        return new Money(this.value.add(other.value));
    }
    public Money subtract(Money other) {
        if (this.value.compareTo(other.value) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        return new Money(this.value.subtract(other.value));
    }


    public boolean isZero(){
        return this.value.compareTo(BigDecimal.ZERO) == 0;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "R$ " + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return value.equals(money.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
