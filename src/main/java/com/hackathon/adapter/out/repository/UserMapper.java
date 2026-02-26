package com.hackathon.adapter.out.repository;

import com.hackathon.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toDomain(UserJpaEntity entity) {
        if (entity == null) return null;
        return User.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .build();
    }

    public UserJpaEntity toJpa(User domain) {
        if (domain == null) return null;
        return UserJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .build();
    }
}
