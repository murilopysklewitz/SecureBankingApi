package com.SecureBankingApi.infrastructure.persistence.user;

import com.SecureBankingApi.domain.user.User;
import com.SecureBankingApi.domain.user.valueObjects.CPF;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {

    public User toDomain(UserJpaEntity entity){
        User user = User.restore(
                entity.getId(),
                entity.getFullName(),
                new CPF(entity.getCpf()),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getRole(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
        return user;
    }

    public UserJpaEntity toEntity(User domain){
        UserJpaEntity entity = new UserJpaEntity(
                domain.getId(),
                domain.getUpdatedAt(),
                domain.getCreatedAt(),
                domain.getStatus(),
                domain.getRole(),
                domain.getPasswordHash(),
                domain.getCpf().getValue(),
                domain.getEmail(),
                domain.getFullName()
        );
        return entity;
    }

    public List<User> toDomainList(List<UserJpaEntity> entities){
        List<User> domains = new ArrayList<>();
        for(UserJpaEntity entity : entities){
            User domain = toDomain(entity);
            domains.add(domain);
        }
        return domains;
    }
    public List<UserJpaEntity> toEntityList(List<User> domains){
        List<UserJpaEntity> entities = new ArrayList<>();
        for(User domain : domains){
            UserJpaEntity entity = toEntity(domain);
            entities.add(entity);
        }
        return entities;
    }
}
