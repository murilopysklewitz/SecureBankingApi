package com.SecureBankingApi.domain;

import com.SecureBankingApi.domain.user.User;
import com.SecureBankingApi.domain.user.enums.UserRole;
import com.SecureBankingApi.domain.user.enums.UserStatus;
import com.SecureBankingApi.domain.user.valueObjects.CPF;
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
        User user = User.create(
                "test@gmail.com",
                "tests test",
                new CPF("111111111111"),
                "testPassword",
                UserRole.USER
        );

        assertNotNull(user);
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }
}
