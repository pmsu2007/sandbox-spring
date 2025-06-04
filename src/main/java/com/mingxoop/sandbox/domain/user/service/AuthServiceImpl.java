package com.mingxoop.sandbox.domain.user.service;

import com.mingxoop.sandbox.domain.user.controller.request.UserCreate;
import com.mingxoop.sandbox.domain.user.repository.UserRepository;
import com.mingxoop.sandbox.domain.user.repository.entity.Role;
import com.mingxoop.sandbox.domain.user.repository.entity.UserEntity;
import com.mingxoop.sandbox.global.api.ApiException;
import com.mingxoop.sandbox.global.api.AppHttpStatus;
import com.mingxoop.sandbox.global.api.PkResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public PkResponse signup(UserCreate userCreate) {

        if (userRepository.existsByEmail(userCreate.getEmail())) {
            throw new ApiException(AppHttpStatus.CONFLICT_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(userCreate.getPassword());

        UserEntity userEntity = userRepository.save(UserEntity.builder()
                .email(userCreate.getEmail())
                .password(encodedPassword)
                .role(Role.USER)
                .build());

        return PkResponse.of(userEntity.getId());
    }
}
