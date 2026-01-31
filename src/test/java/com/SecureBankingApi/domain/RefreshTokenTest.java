package com.SecureBankingApi.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RefreshTokenTest {
    public static RefreshToken createValidRefreshToken(){
        return RefreshToken.create(
                UUID.randomUUID(),
                LocalDateTime.now().plusDays(7)
        );
    }

    @Test
    void ShouldCreateRefreshToken() {
        RefreshToken token = createValidRefreshToken();


        assertNotNull(token);
        assertNotNull(token.getId());
        assertNotNull(token.getToken());
        assertNotNull(token.getUserId());
        assertNotNull(token.getCreatedAt());
        assertNotNull(token.getExpiresAt());

        assertEquals(36, token.getToken().length());

        assertFalse(token.isExpired());
        assertFalse(token.isRevoked());

        assertTrue(token.isValid());
    }
}
