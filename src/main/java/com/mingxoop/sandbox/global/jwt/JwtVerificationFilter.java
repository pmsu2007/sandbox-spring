package com.mingxoop.sandbox.global.jwt;

import com.mingxoop.sandbox.domain.user.repository.AccessTokenBlacklistRepository;
import com.mingxoop.sandbox.global.api.AppHttpStatus;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtVerificationFilter extends OncePerRequestFilter {

	private final JwtRepository jwtRepository;
	private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		String accessToken = jwtRepository.resolveAccessToken(request);

		// Access Token 없으면 다음 필터로 넘어가기
		if (!StringUtils.hasText(accessToken)) {
			filterChain.doFilter(request, response);
			return;
		}

        // 토큰 유효성 검사
		Claims claims;
		try {
			claims = jwtRepository.parseToken(accessToken);
		} catch (JwtException e) {
			log.error("JWT 토큰 검증 실패: {}", e.getMessage());
			throw new JwtAuthenticationException(AppHttpStatus.UNAUTHORIZED.getMessage());
		}

		String jti = claims.getId();
		if (StringUtils.hasText(jti) && accessTokenBlacklistRepository.existsByJti(jti)) {
			log.warn("블랙리스트에 등록된 Access Token (jti={})", jti);
			throw new JwtAuthenticationException(AppHttpStatus.UNAUTHORIZED.getMessage());
		}

		setAuthenticationToSecurityContextHolder(accessToken);
		filterChain.doFilter(request, response);
	}

	private void setAuthenticationToSecurityContextHolder(String accessToken) {
		Authentication authentication = jwtRepository.getAuthentication(accessToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}