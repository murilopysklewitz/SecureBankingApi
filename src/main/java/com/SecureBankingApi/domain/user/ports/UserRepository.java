package com.SecureBankingApi.domain.user.ports;

import com.SecureBankingApi.domain.user.User;
import com.SecureBankingApi.domain.user.enums.UserStatus;
import com.SecureBankingApi.domain.user.valueObjects.CPF;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
     User save(User user) throws Exception;

     Optional<User> findById(UUID id);
     Optional<User> findByEmail(String email);
     Optional<User> findByCpf(CPF cpf);
     List<User> findByStatus(UserStatus status);

     boolean existsByCpf(CPF cpf);
     boolean existsByEmail(String email);
}
