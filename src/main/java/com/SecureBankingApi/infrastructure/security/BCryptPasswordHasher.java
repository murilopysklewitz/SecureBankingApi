package com.SecureBankingApi.infrastructure.security;

import com.SecureBankingApi.domain.user.ports.PasswordHasher;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHasher implements PasswordHasher {
    @Override
    public String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    @Override
    public boolean compare(String raw, String hashed) {
        return BCrypt.checkpw(raw, hashed);
    }
}
