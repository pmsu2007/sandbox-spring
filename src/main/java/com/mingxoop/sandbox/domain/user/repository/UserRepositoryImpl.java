package com.mingxoop.sandbox.domain.user.repository;

import com.mingxoop.sandbox.domain.user.repository.entity.UserEntity;
import com.mingxoop.sandbox.domain.user.repository.jpa.UserJpaRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final JPAQueryFactory query;
    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }
}
