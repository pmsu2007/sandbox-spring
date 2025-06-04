package com.mingxoop.sandbox.global.jwt;

import java.io.IOException;

import com.mingxoop.sandbox.domain.user.controller.request.UserLogin;
import com.mingxoop.sandbox.domain.user.repository.RefreshTokenRepository;
import com.mingxoop.sandbox.domain.user.repository.entity.RefreshTokenEntity;
import com.mingxoop.sandbox.domain.user.repository.entity.UserEntity;
import com.mingxoop.sandbox.global.api.AppCookie;
import com.mingxoop.sandbox.global.api.AppHttpStatus;
import com.mingxoop.sandbox.global.api.BaseResponse;
import com.mingxoop.sandbox.global.api.ErrorResponse;
import com.mingxoop.sandbox.global.security.AppUserDetails;
import com.mingxoop.sandbox.global.util.HashingUtils;
import com.mingxoop.sandbox.global.util.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final String BEARER_PREFIX = "Bearer";
	private final String AUTHORIZATION_HEADER = "Authorization";
	private final String REFRESH_HEADER = "Refresh";
	private final String CONTENT_TYPE = "application/json;charset=UTF-8";
	private final String COOKIE_HEADER = "Set-Cookie";

	private final AuthenticationManager authenticationManager;
	private final AppCookie appCookie;
	private final JwtRepository jwtRepository;
	private final RefreshTokenRepository refreshTokenRepository;

	@SneakyThrows
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		ObjectMapper objectMapper = new ObjectMapper();
		UserLogin userLogin = objectMapper.readValue(request.getInputStream(), UserLogin.class);

		UsernamePasswordAuthenticationToken authToken =
				new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword());

		return authenticationManager.authenticate(authToken);
	}

	@Override
	protected void successfulAuthentication(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain chain,
			Authentication authResult
	) {
		AppUserDetails principal = (AppUserDetails) authResult.getPrincipal();

		// JWT 생성
		TokenResponse token = jwtRepository.generateToken(principal.getId(), principal.getEmail(), principal.getRole());

		// Response Header 설정
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

		Long userId = principal.getId();
		String userAgent = request.getHeader("User-Agent");

		refreshTokenRepository
				.findValidByUserIdAndUserAgent(principal.getId(), request.getHeader("User-Agent"))
				.ifPresent(refreshTokenRepository::delete);

		// 로그인 성공 시 Refresh Token 저장
		RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
				.token(HashingUtils.sha256Base64(token.getRefreshToken()))
				.userAgent(userAgent)
				.expiresAt(token.getRefreshTokenExpiration())
				.isRevoked(false)
				.user(UserEntity.ref(userId))
				.build();

		refreshTokenRepository.save(refreshTokenEntity);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
											  AuthenticationException failed) throws IOException {
		log.warn("AuthenticationException Error: {}", failed.getMessage());

		response.setContentType(CONTENT_TYPE);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		ObjectMapper objectMapper = new ObjectMapper();
		String body = objectMapper.writeValueAsString(BaseResponse.error(ErrorResponse.of(AppHttpStatus.UNAUTHORIZED)));
		response.getWriter().write(body);
	}
}