package com.mingxoop.sandbox.domain.user.service;

import com.mingxoop.sandbox.domain.user.controller.request.UserCreate;
import com.mingxoop.sandbox.domain.user.repository.AccessTokenBlacklistRepository;
import com.mingxoop.sandbox.domain.user.repository.RefreshTokenRepository;
import com.mingxoop.sandbox.domain.user.repository.UserRepository;
import com.mingxoop.sandbox.domain.user.repository.entity.AccessTokenBlacklistEntity;
import com.mingxoop.sandbox.domain.user.repository.entity.RefreshTokenEntity;
import com.mingxoop.sandbox.domain.user.repository.entity.Role;
import com.mingxoop.sandbox.domain.user.repository.entity.UserEntity;
import com.mingxoop.sandbox.global.api.ApiException;
import com.mingxoop.sandbox.global.api.AppCookie;
import com.mingxoop.sandbox.global.api.AppHttpStatus;
import com.mingxoop.sandbox.global.api.PkResponse;
import com.mingxoop.sandbox.global.jwt.JwtRepository;
import com.mingxoop.sandbox.global.jwt.TokenResponse;
import com.mingxoop.sandbox.global.util.HashingUtils;
import com.mingxoop.sandbox.global.util.TimeUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final String BEARER_PREFIX = "Bearer";
    private final String AUTHORIZATION_HEADER = "Authorization";
    private final String REFRESH_HEADER = "Refresh";
    private final String COOKIE_HEADER = "Set-Cookie";

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;
    private final JwtRepository jwtRepository;
    private final AppCookie appCookie;
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

    @Override
    @Transactional
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // 1. 쿠키에서 리프레시 토큰 추출
        String userRefreshToken = jwtRepository.resolveRefreshToken(request);
        if (!StringUtils.hasText(userRefreshToken)) {
            throw new ApiException(AppHttpStatus.MISSING_REFRESH_TOKEN);
        }

        Claims claims = jwtRepository.parseToken(userRefreshToken);
        Long userId = Long.valueOf(claims.get("id").toString());

        // 2. HTTP Request에서 User Agent 추출
        String userAgent = request.getHeader("User-Agent");
        if (!StringUtils.hasText(userAgent)) {
            throw new ApiException(AppHttpStatus.MISSING_USER_AGENT);
        }

        // 3. 리프레시 토큰 검증
        RefreshTokenEntity serverRefreshToken = refreshTokenRepository.findValidByUserIdAndUserAgent(userId, userAgent)
                .orElseThrow(() -> new ApiException(AppHttpStatus.NOT_FOUND_REFRESH_TOKEN));

        if (!HashingUtils.sha256Base64(userRefreshToken).equals(serverRefreshToken.getToken())) {
            // 토큰 불일치 시 해당 유저의 리프레시 토큰 전체 삭제
            log.warn("Refresh Token reuse detected for userId={}, userAgent={}", userId, userAgent);
            refreshTokenRepository.deleteByUserId(userId);
            throw new ApiException(AppHttpStatus.REUSE_DETECTED_REFRESH_TOKEN);
        }

        if (serverRefreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.deleteById(serverRefreshToken.getId());
            throw new ApiException(AppHttpStatus.EXPIRED_REFRESH_TOKEN);
        }

        // 4. 사용자 정보 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(AppHttpStatus.NOT_FOUND_USER));


        // 5. 토큰 재발급
        refreshTokenRepository.deleteById(serverRefreshToken.getId());
        TokenResponse token = jwtRepository.generateToken(user);
        refreshTokenRepository.save(RefreshTokenEntity.builder()
                .token(HashingUtils.sha256Base64(token.getRefreshToken()))
                .userAgent(userAgent)
                .expiresAt(token.getRefreshTokenExpiration())
                .isRevoked(false)
                .user(UserEntity.ref(userId))
                .build());

        // 6. Response Header 설정
        response.addHeader(
                COOKIE_HEADER,
                appCookie.createCookie(
                        AUTHORIZATION_HEADER,
                        BEARER_PREFIX + token.getAccessToken(),
                        TimeUtils.secondsUntil(token.getAccessTokenExpiration())
                )
        );
        response.addHeader(
                COOKIE_HEADER,
                appCookie.createCookie(
                        REFRESH_HEADER,
                        token.getRefreshToken(),
                        TimeUtils.secondsUntil(token.getRefreshTokenExpiration())
                )
        );
    }

    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 1. 토큰 추출
        String userAccessToken = jwtRepository.resolveAccessToken(request);
        if (!StringUtils.hasText(userAccessToken)) {
            throw new ApiException(AppHttpStatus.MISSING_ACCESS_TOKEN);
        }

        String userRefreshToken = jwtRepository.resolveRefreshToken(request);
        if (!StringUtils.hasText(userRefreshToken)) {
            throw new ApiException(AppHttpStatus.MISSING_REFRESH_TOKEN);
        }

        // 2. 리프레시 토큰 파싱 및 유저 식별
        Claims refreshClaims = jwtRepository.parseToken(userRefreshToken);
        Long userId = Long.valueOf(refreshClaims.get("id").toString());

        // 3. User Agent 추출
        String userAgent = request.getHeader("User-Agent");
        if (!StringUtils.hasText(userAgent)) {
            throw new ApiException(AppHttpStatus.MISSING_USER_AGENT);
        }

        // 4. Refresh Token Reuse 검증
        RefreshTokenEntity serverRefreshToken = refreshTokenRepository.findValidByUserIdAndUserAgent(userId, userAgent)
                .orElse(null);

        if (serverRefreshToken != null) {
            if (!HashingUtils.sha256Base64(userRefreshToken).equals(serverRefreshToken.getToken())) {
                // 토큰 불일치 시 해당 유저의 리프레시 토큰 전체 삭제
                log.warn("Refresh Token reuse detected for userId={}, userAgent={}", userId, userAgent);
                refreshTokenRepository.deleteByUserId(userId);
                throw new ApiException(AppHttpStatus.REUSE_DETECTED_REFRESH_TOKEN);
            }

            // 정상 리프레시 토큰일 경우 삭제
            refreshTokenRepository.deleteById(serverRefreshToken.getId());
        }

        // 5. 액세스 토큰 무효화
        Claims accessClaims = jwtRepository.parseToken(userAccessToken);
        if (!StringUtils.hasText(accessClaims.getId()) || accessClaims.getExpiration() == null) {
            throw new ApiException(AppHttpStatus.INVALID_TOKEN);
        }

        String jti = accessClaims.getId();
        LocalDateTime expiration = accessClaims.getExpiration()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        accessTokenBlacklistRepository.save(AccessTokenBlacklistEntity.builder()
                .jti(jti)
                .expiresAt(expiration)
                .user(UserEntity.ref(userId))
                .build());

        // 6. 쿠키 제거
        response.addHeader(
                COOKIE_HEADER,
                appCookie.deleteCookie(AUTHORIZATION_HEADER)
        );
        response.addHeader(
                COOKIE_HEADER,
                appCookie.deleteCookie(REFRESH_HEADER)
        );
    }
}
