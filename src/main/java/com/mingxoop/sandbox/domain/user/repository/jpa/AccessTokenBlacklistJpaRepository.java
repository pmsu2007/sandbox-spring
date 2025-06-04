package com.mingxoop.sandbox.domain.user.repository.jpa;

import com.mingxoop.sandbox.domain.user.repository.entity.AccessTokenBlacklistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessTokenBlacklistJpaRepository extends JpaRepository<AccessTokenBlacklistEntity, Long> {
    boolean existsByJti(String jti);
}
