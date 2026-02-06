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

    public Money add(Money other){
        return new Money(this.value.add(other.value));
    }
    public Money subtract(Money other){
        return new Money(this.value.subtract(other.value));
    }


    public boolean isZero(){
        return this.value.equals(BigDecimal.ZERO);
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "R$ " + value;
    }
}
