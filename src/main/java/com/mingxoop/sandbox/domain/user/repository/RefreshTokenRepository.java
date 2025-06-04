package com.mingxoop.sandbox.domain.user.repository;

import com.mingxoop.sandbox.domain.user.repository.entity.RefreshTokenEntity;

public interface RefreshTokenRepository {
    RefreshTokenEntity save(RefreshTokenEntity refreshTokenEntity);
}
