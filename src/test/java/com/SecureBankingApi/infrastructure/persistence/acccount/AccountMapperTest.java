package com.SecureBankingApi.infrastructure.persistence.acccount;

import com.SecureBankingApi.domain.account.Account;
import com.SecureBankingApi.domain.account.AccountNumber;
import com.SecureBankingApi.domain.account.AccountStatus;
import com.SecureBankingApi.domain.account.AccountType;
import com.SecureBankingApi.infrastructure.persistence.account.AccountJpaEntity;
import com.SecureBankingApi.infrastructure.persistence.account.AccountMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountMapperTest {
    private AccountMapper mapper;

    @BeforeEach
    void SetUp() {
        mapper = new AccountMapper();
    }

    @Test
    void ShouldConvertEntityToDomain() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(15);

        AccountJpaEntity entity = new AccountJpaEntity(
                id,
                AccountNumber.generate().getValue(),
                "001",
                userId,
                amount,
                AccountType.CHECKING,
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        Account domain = mapper.toDomain(entity);

        assertEquals(entity.getId(), domain.getId());
        assertEquals(entity.getAccountNumber(), domain.getAccountNumber().getValue());
        assertEquals(entity.getAgency(), domain.getAgency());
        assertEquals(entity.getBalance(), domain.getBalance().getValue());
        assertEquals(entity.getType(), domain.getType());
        assertEquals(entity.getStatus(), domain.getStatus());
        assertEquals(entity.getUserId(), domain.getUserId());
        assertEquals(entity.getCreated_at(), domain.getCreatedAt());
        assertEquals(entity.getUpdated_at(), domain.getUpdatedAt());
    }

    @Test
    void ShouldConvertDomainToEntity() {
        Account domain = Account.create(
                AccountNumber.generate(),
                "001",
                UUID.randomUUID(),
                AccountType.CHECKING
        );

        AccountJpaEntity entity = mapper.toEntity(domain);

        assertEquals(entity.getId(), domain.getId());
        assertEquals(entity.getAccountNumber(), domain.getAccountNumber().getValue());
        assertEquals(entity.getAgency(), domain.getAgency());
        assertEquals(entity.getBalance(), domain.getBalance().getValue());
        assertEquals(entity.getType(), domain.getType());
        assertEquals(entity.getStatus(), domain.getStatus());
        assertEquals(entity.getUserId(), domain.getUserId());
        assertEquals(entity.getCreated_at(), domain.getCreatedAt());
        assertEquals(entity.getUpdated_at(), domain.getUpdatedAt());
    }
}
