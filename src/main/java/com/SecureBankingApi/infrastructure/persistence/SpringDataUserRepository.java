package com.SecureBankingApi.infrastructure.persistence;

import com.SecureBankingApi.domain.user.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, UUID> {
    List<UserJpaEntity> findByStatus(UserStatus status);
    Optional<UserJpaEntity> findByEmail(String email);
    Optional<UserJpaEntity> findById(UUID id);
    Optional<UserJpaEntity> findByCpf(String cpf);

    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
}
