package com.SecureBankingApi.infrastructure.persistence.acccount;

import com.SecureBankingApi.domain.account.*;
import com.SecureBankingApi.infrastructure.persistence.account.AccountJpaEntity;
import com.SecureBankingApi.infrastructure.persistence.account.AccountMapper;
import com.SecureBankingApi.infrastructure.persistence.account.AccountRepositoryAdapter;
import com.SecureBankingApi.infrastructure.persistence.account.SpringDataAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountRepositoryAdapterTest {
    @Mock
    private SpringDataAccountRepository springDataRepository;
    @Mock
    private AccountMapper accountMapper;
    @InjectMocks
    private AccountRepositoryAdapter adapter;
    private UUID accountId;
    private AccountNumber accountNumber;
    private UUID userId;
    private Account domainAccount;
    private AccountJpaEntity entityAccount;

    @BeforeEach
    void SetUp() {
        accountId = UUID.randomUUID();
        accountNumber = AccountNumber.generate();
        userId = UUID.randomUUID();
        domainAccount = Account.restore(
                accountId,
                userId,
                accountNumber.getValue(),
                "001",
                Money.of(BigDecimal.valueOf(10)),
                AccountStatus.ACTIVE,
                AccountType.CHECKING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        entityAccount = new AccountJpaEntity(
                accountId,
                accountNumber.getValue(),
                "001",
                userId,
                BigDecimal.valueOf(10),
                AccountType.CHECKING,
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()

        );
    }
    @Test
    void shouldSaveAccountSuccessfully() {
        when(accountMapper.toEntity(domainAccount)).thenReturn(entityAccount);
        when(springDataRepository.save(entityAccount)).thenReturn(entityAccount);
        adapter.save(domainAccount);
        verify(accountMapper, times(1)).toEntity(domainAccount);
        verify(springDataRepository, times(1)).save(entityAccount);
    }

    @Test
    void shouldFindAccountByIdSuccessfully() {
        when(springDataRepository.findById(accountId)).thenReturn(Optional.of(entityAccount));
        when(accountMapper.toDomain(entityAccount)).thenReturn(domainAccount);

        Optional<Account> result = adapter.findById(accountId);

        assertTrue(result.isPresent());
        assertEquals(domainAccount, result.get());
        verify(springDataRepository, times(1)).findById(accountId);
        verify(accountMapper, times(1)).toDomain(entityAccount);
    }

    @Test
    void shouldReturnEmptyWhenAccountNotFoundById() {
        when(springDataRepository.findById(accountId)).thenReturn(Optional.empty());

        Optional<Account> result = adapter.findById(accountId);

        assertFalse(result.isPresent());
        verify(springDataRepository, times(1)).findById(accountId);
        verify(accountMapper, never()).toDomain(any());
    }

    @Test
    void shouldFindAccountByAccountNumberSuccessfully() {
        // Arrange
        String accountNumber = AccountRepositoryAdapterTest.this.accountNumber.getValue();
        when(springDataRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(entityAccount));
        when(accountMapper.toDomain(entityAccount)).thenReturn(domainAccount);

        // Act
        Optional<Account> result = adapter.findByAccountNumber(AccountNumber.restore(accountNumber));

        // Assert
        assertTrue(result.isPresent());
        assertEquals(domainAccount, result.get());
        verify(springDataRepository, times(1)).findByAccountNumber(accountNumber);
        verify(accountMapper, times(1)).toDomain(entityAccount);
    }

    @Test
    void shouldReturnEmptyWhenAccountNotFoundByAccountNumber() {

        String accountNumber = AccountNumber.generate().getValue();
        when(springDataRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.empty());

        Optional<Account> result = adapter.findByAccountNumber(AccountNumber.restore(accountNumber));

        assertFalse(result.isPresent());
        verify(springDataRepository, times(1)).findByAccountNumber(accountNumber);
        verify(accountMapper, never()).toDomain(any());
    }

    @Test
    void shouldFindAccountsByUserIdSuccessfully() {
        AccountJpaEntity entity2 = new AccountJpaEntity(
                UUID.randomUUID(),
                AccountNumber.generate().getValue(),
                "001",
                userId,
                BigDecimal.valueOf(500.00),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Account domainAccount2 = Account.restore(
                entity2.getId(),
                userId,
                AccountNumber.generate().getValue(),
                "001",
                Money.of(BigDecimal.valueOf(500.00)),
                AccountStatus.ACTIVE,
                AccountType.SAVINGS,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        List<AccountJpaEntity> entities = List.of(entityAccount, entity2);

        when(springDataRepository.findByUserId(userId)).thenReturn(entities);
        when(accountMapper.toDomain(entityAccount)).thenReturn(domainAccount);
        when(accountMapper.toDomain(entity2)).thenReturn(domainAccount2);

        List<Account> result = adapter.findByUserId(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(domainAccount, result.get(0));
        assertEquals(domainAccount2, result.get(1));
        verify(springDataRepository, times(1)).findByUserId(userId);
        verify(accountMapper, times(2)).toDomain(any(AccountJpaEntity.class));
    }

    @Test
    void shouldReturnEmptyListWhenNoAccountsFoundForUser() {


        UUID differentUserId = UUID.randomUUID();
        when(springDataRepository.findByUserId(differentUserId))
                .thenReturn(List.of());

        List<Account> result = adapter.findByUserId(differentUserId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(springDataRepository, times(1)).findByUserId(differentUserId);
        verify(accountMapper, never()).toDomain(any());


    }

    @Test
    void shouldFindAllAccountsSuccessfully() {


        AccountJpaEntity entity2 = new AccountJpaEntity(
                UUID.randomUUID(),
                "65432-1",
                "001",
                UUID.randomUUID(),
                BigDecimal.valueOf(500.00),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        List<AccountJpaEntity> entities = List.of(entityAccount, entity2);
        List<Account> domainAccounts = List.of(domainAccount);

        when(springDataRepository.findAll()).thenReturn(entities);
        when(accountMapper.toDomainList(entities)).thenReturn(domainAccounts);

        List<Account> result = adapter.findAll();


        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(springDataRepository, times(1)).findAll();
        verify(accountMapper, times(1)).toDomainList(entities);
    }

    @Test
    void shouldReturnEmptyListWhenNoAccountsExist() {


        when(springDataRepository.findAll()).thenReturn(List.of());
        when(accountMapper.toDomainList(List.of())).thenReturn(List.of());

        List<Account> result = adapter.findAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(springDataRepository, times(1)).findAll();
        verify(accountMapper, times(1)).toDomainList(List.of());


    }

    @Test
    void shouldCheckIfAccountExistsByAccountNumber() {

        String accountNumberStr = AccountRepositoryAdapterTest.this.accountNumber.getValue();
        when(springDataRepository.existsByAccountNumber(accountNumberStr))
                .thenReturn(true);

        boolean exists = adapter.existsByAccountNumber(AccountNumber.restore(accountNumberStr));

        assertTrue(exists);
        verify(springDataRepository, times(1)).existsByAccountNumber(accountNumberStr);
    }

    @Test
    void shouldReturnFalseWhenAccountDoesNotExistByAccountNumber() {
        String accountNumber = AccountNumber.generate().getValue();
        when(springDataRepository.existsByAccountNumber(accountNumber))
                .thenReturn(false);

        boolean exists = adapter.existsByAccountNumber(AccountNumber.restore(accountNumber));


        assertFalse(exists);
        verify(springDataRepository, times(1)).existsByAccountNumber(accountNumber);
    }

    @Test
    void shouldDeleteAccountSuccessfully() {


        doNothing().when(springDataRepository).deleteById(accountId);

        adapter.delete(domainAccount);

        verify(springDataRepository, times(1)).deleteById(accountId);



    }

    @Test
    void shouldUpdateAccountBalanceWhenSaving() {


        Money newBalance = Money.of(BigDecimal.valueOf(2000.00));
        domainAccount.credit(Money.of(BigDecimal.valueOf(1000.00)));

        AccountJpaEntity updatedEntity = new AccountJpaEntity(
                accountId,
                accountNumber.getValue(),
                "001",
                userId,
                BigDecimal.valueOf(2000.00),
                AccountType.CHECKING,
                AccountStatus.ACTIVE,
                entityAccount.getCreated_at(),
                LocalDateTime.now()
        );

        when(accountMapper.toEntity(domainAccount)).thenReturn(updatedEntity);
        when(springDataRepository.save(updatedEntity)).thenReturn(updatedEntity);

        adapter.save(domainAccount);

        verify(accountMapper, times(1)).toEntity(domainAccount);
        verify(springDataRepository, times(1)).save(updatedEntity);


    }

    @Test
    void shouldFindAccountsByStatus() {


        List<AccountJpaEntity> entities = List.of(entityAccount);
        List<Account> domainAccounts = List.of(domainAccount);
        when(springDataRepository.findByStatus(AccountStatus.ACTIVE))
                .thenReturn(entities);
        List<AccountJpaEntity> result = springDataRepository.findByStatus(AccountStatus.ACTIVE);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(AccountStatus.ACTIVE, result.get(0).getStatus());
        verify(springDataRepository, times(1)).findByStatus(AccountStatus.ACTIVE);


    }

    @Test
    void shouldFindAccountsByType() {
        List<AccountJpaEntity> entities = List.of(entityAccount);

        when(springDataRepository.findByType(AccountType.CHECKING))
                .thenReturn(entities);

        List<AccountJpaEntity> result = springDataRepository.findByType(AccountType.CHECKING);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(AccountType.CHECKING, result.get(0).getType());
        verify(springDataRepository, times(1)).findByType(AccountType.CHECKING);
    }

    @Test
    void shouldHandleMultipleAccountsForSameUser() {
        AccountJpaEntity checkingEntity = new AccountJpaEntity(
                UUID.randomUUID(),
                AccountNumber.generate().getValue(),
                "001",
                userId,
                BigDecimal.valueOf(1000.00),
                AccountType.CHECKING,
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        AccountJpaEntity savingsEntity = new AccountJpaEntity(
                UUID.randomUUID(),
                AccountNumber.generate().getValue(),
                "001",
                userId,
                BigDecimal.valueOf(5000.00),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        List<AccountJpaEntity> entities = List.of(checkingEntity, savingsEntity);

        when(springDataRepository.findByUserId(userId)).thenReturn(entities);

        when(accountMapper.toDomain(any(AccountJpaEntity.class)))
                .thenAnswer(invocation -> {
                    AccountJpaEntity entity = invocation.getArgument(0);
                    return Account.restore(
                            entity.getId(),
                            entity.getUserId(),
                            entity.getAccountNumber(),
                            entity.getAgency(),
                            Money.of(entity.getBalance()),
                            entity.getStatus(),
                            entity.getType(),
                            entity.getCreated_at(),
                            entity.getUpdated_at()
                    );


                });

        List<Account> result = adapter.findByUserId(userId);

        assertNotNull(result);
        assertEquals(2, result.size());

        boolean hasChecking = result.stream()
                .anyMatch(acc -> acc.getType() == AccountType.CHECKING);
        boolean hasSavings = result.stream()
                .anyMatch(acc -> acc.getType() == AccountType.SAVINGS);

        assertTrue(hasChecking);
        assertTrue(hasSavings);

        verify(springDataRepository, times(1)).findByUserId(userId);
        verify(accountMapper, times(2)).toDomain(any(AccountJpaEntity.class));


    }
}
