package com.mingxoop.sandbox.domain.user.repository;

import com.mingxoop.sandbox.domain.user.repository.entity.AccessTokenBlacklistEntity;
import com.mingxoop.sandbox.domain.user.repository.jpa.AccessTokenBlacklistJpaRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AccessTokenBlacklistRepositoryImpl implements AccessTokenBlacklistRepository {

    private final AccessTokenBlacklistJpaRepository accessTokenBlacklistJpaRepository;
    private final JPAQueryFactory query;

    @Override
    public AccessTokenBlacklistEntity save(AccessTokenBlacklistEntity accessTokenBlacklistEntity) {
        return accessTokenBlacklistJpaRepository.save(accessTokenBlacklistEntity);
    }
}
