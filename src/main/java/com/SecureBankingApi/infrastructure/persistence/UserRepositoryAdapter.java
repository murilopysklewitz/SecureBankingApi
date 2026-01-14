package com.SecureBankingApi.infrastructure.persistence;

import com.SecureBankingApi.domain.User;
import com.SecureBankingApi.domain.UserRepository;
import com.SecureBankingApi.domain.UserStatus;
import com.SecureBankingApi.domain.valueObjects.CPF;

import java.util.List;
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
            throw new RuntimeException("[USER REPOSITORY] Failed to save user", e);
        }
    }

    @Override
    public Optional<User> findById(UUID id) {
        try{
            Optional<UserJpaEntity> entity = userRepository.findById(id);
            return entity.map((mapper::toDomain));
        } catch (Exception e) {
            throw new RuntimeException("[USER REPOSITORY] Failed to find by id user",e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try{
            Optional<UserJpaEntity> entity = userRepository.findByEmail(email);
            return entity.map((mapper::toDomain));
        } catch (Exception e) {
            throw new RuntimeException("[USER REPOSITORY] Failed to find by email user",e);
        }
    }

    @Override
    public Optional<User> findByCpf(CPF cpf) {
        try{
            Optional<UserJpaEntity> entity = userRepository.findByCpf(cpf.getValue());
            return entity.map((mapper::toDomain));
        } catch (Exception e) {
            throw new RuntimeException("[USER REPOSITORY] Failed to find by email user",e);
        }
    }

    @Override
    public List<User> findByStatus(UserStatus status) {
        try {
            List<UserJpaEntity> entities = userRepository.findByStatus(status);
            return mapper.toDomainList(entities);
        } catch (Exception e) {
            throw new RuntimeException("[USER REPOSITORY] Failed to find users by status",e);
        }
    }

    @Override
    public boolean existsByCpf(CPF cpf) {
        return userRepository.existsByCpf(cpf.getValue());
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
