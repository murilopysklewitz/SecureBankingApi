package com.SecureBankingApi.domain;

import com.SecureBankingApi.domain.valueObjects.CPF;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class UserTest {

    public static User createValidUser() {
        return User.create(
                "murilo@gmail.com",
                "Murilo Pysklewitz",
                new CPF("12345678901"),
                "1234567",
                UserRole.USER
        );

    }
    @Test
    void shouldCreateAValidUser() {
        User user = createValidUser();

        assertNotNull(user);
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }
}
