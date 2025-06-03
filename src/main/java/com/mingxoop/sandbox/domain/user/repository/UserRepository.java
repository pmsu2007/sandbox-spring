package com.mingxoop.sandbox.domain.user.repository;

import com.mingxoop.sandbox.domain.user.repository.entity.UserEntity;

import java.util.Optional;

public interface UserRepository {
    Optional<UserEntity> findByEmail(String email);
}
