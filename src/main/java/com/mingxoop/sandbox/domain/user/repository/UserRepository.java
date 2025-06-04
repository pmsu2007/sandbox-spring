package com.mingxoop.sandbox.domain.user.repository;

import com.mingxoop.sandbox.domain.user.repository.entity.UserEntity;

import java.util.Optional;

public interface UserRepository {
    UserEntity save(UserEntity userEntity);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findById(Long userId);
    boolean existsByEmail(String email);
}
