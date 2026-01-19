package com.SecureBankingApi.domain.user.valueObjects;


public class CPF {

    private final String value;

    public CPF(String value){
        if(!value.matches("\\d{11}")){
            throw new IllegalArgumentException("INVALID CPF");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
