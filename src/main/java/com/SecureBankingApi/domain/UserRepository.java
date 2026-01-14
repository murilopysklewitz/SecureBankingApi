package com.SecureBankingApi.domain;

import com.SecureBankingApi.domain.valueObjects.CPF;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
     void save(User user) throws Exception;

     Optional<User> findById(UUID id);
     Optional<User> findByEmail(String email);
     Optional<User> findByCpf(CPF cpf);

     boolean existsByCpf(CPF cpf);
     boolean existsByEmail(String email);
}
