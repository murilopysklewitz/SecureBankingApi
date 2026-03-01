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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    void ShouldFindByIdUserSuccessfully(){
        UUID id = UUID.randomUUID();
        when(springRepository.findById(id)).thenReturn(Optional.of(entityUser));
        when(mapper.toDomain(entityUser)).thenReturn(domainUser);

        Optional<User> result = adapter.findById(id);

        assertTrue(result.isPresent());
        assertEquals(domainUser, result.get());
        verify(springRepository, times(1)).findById(userId);
        verify(mapper, times(1)).toDomain(entityUser);

    }

    @Test
    void ShouldReturnEmptyWhenUserNotFound() {
            when(springRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            Optional<User> result = adapter.findById(userId);

            assertTrue(result.isEmpty());
            verify(springRepository, times(1)).findById(userId);
            verify(mapper, never()).toDomain(any());

    }

    @Test
    void ShouldFindByEmailUserSuccessfully() {
        String email = "user@gmail.com";
        when(springRepository.findByEmail(email))
                .thenReturn(Optional.of(entityUser));

        when(mapper.toDomain(entityUser))
                .thenReturn(domainUser);

        Optional<User> result = adapter.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(domainUser, result.get());

        verify(springRepository, times(1))
                .findByEmail(email);

        verify(mapper, times(1))
                .toDomain(entityUser);

    }

    @Test
    void ShouldFindByCpfSuccessfully(){
        CPF cpf = new CPF("123-456-789-12");
        when(springRepository.findByCpf("123456789-12"))
                .thenReturn(Optional.of(entityUser));

        when(mapper.toDomain(entityUser)).thenReturn(domainUser);

        Optional<User> result = adapter.findByCpf(cpf);

        assertTrue(result.isPresent());
        assertEquals(domainUser, result.get());

        verify(springRepository, times(1))
                .findByCpf(cpf.getValue());
    }

    @Test
    void ShouldFindByStatusSuccessfully() {
        UserStatus status = UserStatus.ACTIVE;
        when(springRepository.findByStatus(status)).thenReturn(List.of(entityUser));
        when(mapper.toDomainList(List.of(entityUser))).thenReturn(List.of(domainUser));

        List<User> result = adapter.findByStatus(status);


        verify(springRepository, times(1)).findByStatus(status);
        verify(mapper, times(1)).toDomainList(List.of(entityUser));
    }

    @Test
    void ShouldVReturnTrueWhenExistsByCpf(){
        when(springRepository.existsByCpf(entityUser.getCpf())).thenReturn(true);
        boolean result = adapter.existsByCpf(new CPF(entityUser.getCpf()));
        assertTrue(result);

    }

}
