package com.SecureBankingApi.infrastructure.persistence;

import com.SecureBankingApi.domain.UserStatus;
import com.SecureBankingApi.domain.valueObjects.CPF;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, UUID> {
    Optional<UserJpaEntity> findByStatus(UserStatus status);
    Optional<UserJpaEntity> findByEmail(String email);
    Optional<UserJpaEntity> findById(UUID id);

    boolean existsByEmail(String email);
    boolean existsByCpf(CPF cpf);
}
