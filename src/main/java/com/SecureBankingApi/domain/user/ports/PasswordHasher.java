package com.SecureBankingApi.domain.user.ports;

public interface PasswordHasher {
    String hash(String password);
    boolean compare(String raw, String hashed);
}
