package com.mingxoop.sandbox.domain.user.repository;

import com.mingxoop.sandbox.domain.user.repository.entity.AccessTokenBlacklistEntity;

public interface AccessTokenBlacklistRepository {
    AccessTokenBlacklistEntity save(AccessTokenBlacklistEntity accessTokenBlacklistEntity);
}
