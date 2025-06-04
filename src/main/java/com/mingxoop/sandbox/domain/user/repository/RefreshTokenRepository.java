package com.mingxoop.sandbox.domain.user.repository;

import com.mingxoop.sandbox.domain.user.repository.entity.RefreshTokenEntity;

import java.util.Optional;

public interface RefreshTokenRepository {
    RefreshTokenEntity save(RefreshTokenEntity refreshTokenEntity);
    Optional<RefreshTokenEntity> findValidByUserIdAndUserAgent(Long userId, String userAgent);
    void delete(RefreshTokenEntity refreshTokenEntity);
}
