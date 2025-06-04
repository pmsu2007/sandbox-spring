package com.mingxoop.sandbox.domain.user.repository;

import com.mingxoop.sandbox.domain.user.repository.entity.RefreshTokenEntity;
import com.mingxoop.sandbox.domain.user.repository.jpa.RefreshTokenJpaRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.mingxoop.sandbox.domain.user.repository.entity.QRefreshTokenEntity.refreshTokenEntity;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private final JPAQueryFactory query;

    @Override
    public RefreshTokenEntity save(RefreshTokenEntity refreshTokenEntity) {
        return refreshTokenJpaRepository.save(refreshTokenEntity);
    }

    @Override
    public Optional<RefreshTokenEntity> findValidByUserIdAndUserAgent(Long userId, String userAgent) {
        return Optional.ofNullable(
                query.selectFrom(refreshTokenEntity)
                        .where(
                            refreshTokenEntity.user.id.eq(userId),
                            refreshTokenEntity.userAgent.eq(userAgent),
                            refreshTokenEntity.isRevoked.isFalse(),
                            refreshTokenEntity.expiresAt.after(LocalDateTime.now())
                    ).fetchOne()
        );
    }

    @Override
    public void delete(RefreshTokenEntity refreshTokenEntity) {
        refreshTokenJpaRepository.delete(refreshTokenEntity);
    }
}
