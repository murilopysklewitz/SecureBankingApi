package com.SecureBankingApi.infrastructure.persistence;

import com.SecureBankingApi.domain.User;
import com.SecureBankingApi.domain.UserRepository;
import com.SecureBankingApi.domain.valueObjects.CPF;

import java.util.Optional;
import java.util.UUID;

public class UserRepositoryAdapter  implements UserRepository {

    private final SpringDataUserRepository userRepository;
    private final UserMapper mapper;

    public UserRepositoryAdapter(SpringDataUserRepository userRepository, UserMapper mapper){
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(User user) throws Exception {
        try{
            UserJpaEntity entity = mapper.toEntity(user);
            userRepository.save(entity);
        }catch (Exception e){
            throw new RuntimeException("[USER REPOSITORY] não foi possível salvar usuário");
        }
    }

    @Override
    public Optional<User> findById(UUID id) {
        try{
            Optional<UserJpaEntity> entity = userRepository.findById(id);
            Optional<User> domain = Optional.ofNullable(mapper.toDomain(entity));
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByCpf(CPF cpf) {
        return Optional.empty();
    }

    @Override
    public boolean existsByCpf(CPF cpf) {
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }
}
