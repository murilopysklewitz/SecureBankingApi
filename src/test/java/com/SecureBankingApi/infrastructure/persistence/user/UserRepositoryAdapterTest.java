package com.SecureBankingApi.infrastructure.persistence.user;

import com.SecureBankingApi.domain.user.User;
import com.SecureBankingApi.domain.user.enums.UserRole;
import com.SecureBankingApi.domain.user.enums.UserStatus;
import com.SecureBankingApi.domain.user.valueObjects.CPF;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryAdapterTest {
    @Mock
    private SpringDataUserRepository springRepository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserRepositoryAdapter adapter;

    private UUID userId;
    private User domainUser;
    private UserJpaEntity entityUser;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        domainUser = User.restore(
                userId,
                "Joao Bobao",
                new CPF("12345678901"),
                "joao@gmail.com",
                "passwordHashed",
                UserRole.USER,
                UserStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );
        entityUser = new UserJpaEntity(
                userId,
                LocalDateTime.now(),
                LocalDateTime.now().minusDays(1),
                UserStatus.ACTIVE,
                UserRole.USER,
                "passwordHashed",
                "12345678901",
                "joao@gmail.com",
                "Joao Bobao"
        );
    }

    @Test
    @DisplayName("Should save an user correctly")
    void shouldSaveUserSuccessfully() {
        // Arrange
        when(mapper.toEntity(domainUser)).thenReturn(entityUser);
        when(springRepository.save(entityUser)).thenReturn(entityUser);

        // Act
        assertDoesNotThrow(() -> adapter.save(domainUser));

        // Assert
        verify(mapper, times(1)).toEntity(domainUser);
        verify(springRepository, times(1)).save(entityUser);
    }
}
