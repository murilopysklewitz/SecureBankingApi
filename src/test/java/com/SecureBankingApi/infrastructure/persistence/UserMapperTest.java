package com.SecureBankingApi.infrastructure.persistence;

import com.SecureBankingApi.domain.user.User;
import com.SecureBankingApi.domain.user.enums.UserRole;
import com.SecureBankingApi.domain.user.enums.UserStatus;
import com.SecureBankingApi.domain.user.valueObjects.CPF;
import com.SecureBankingApi.infrastructure.persistence.user.UserJpaEntity;
import com.SecureBankingApi.infrastructure.persistence.user.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserMapperTest {
    private UserMapper mapper;

    @BeforeEach
    void setUp(){
        mapper = new UserMapper();
    }
    @Test
    void shouldConvertAnEntityToDomain(){
        // PREPARE
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        UserJpaEntity entity = new UserJpaEntity(
                id,
                now,
                now,
                UserStatus.ACTIVE,
                UserRole.USER,
                "hashedPassword",
                "12345678901",
                "murilo@gmail.com",
                "Murilo Rilo"
        );
        // ACT
        User domain = mapper.toDomain(entity);

        // ASSERT

        assertNotNull(domain);
        assertEquals(id, domain.getId());
        assertEquals("Murilo Rilo", domain.getFullName());
        assertEquals("murilo@gmail.com", domain.getEmail());
        assertEquals("12345678901", domain.getCpf().getValue());
        assertEquals("hashedPassword", domain.getPasswordHash());
        assertEquals(UserRole.USER, domain.getRole());
        assertEquals(UserStatus.ACTIVE, domain.getStatus());
        assertEquals(now, domain.getCreatedAt());
        assertEquals(now, domain.getUpdatedAt());
    }

    @Test
    void shouldConvertADomainToAnEntity() {
        User domain = User.create(
                "murilo@gmail.com",
                "Murilo Rilo",
                new CPF("12345678901"),
                "hashedPassword",
                UserRole.USER
        );


        UserJpaEntity entity = mapper.toEntity(domain);

        assertEquals("Murilo Rilo", entity.getFullName());
        assertEquals("murilo@gmail.com", entity.getEmail());
        assertEquals("12345678901", entity.getCpf());
        assertEquals("hashedPassword", entity.getPasswordHash());
        assertEquals(UserRole.USER, entity.getRole());
        assertEquals(UserStatus.ACTIVE, entity.getStatus());

    }
}
