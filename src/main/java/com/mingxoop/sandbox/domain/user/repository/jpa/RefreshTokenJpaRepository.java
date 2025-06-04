package com.mingxoop.sandbox.domain.user.repository.jpa;

import com.mingxoop.sandbox.domain.user.repository.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, Long> {
}
