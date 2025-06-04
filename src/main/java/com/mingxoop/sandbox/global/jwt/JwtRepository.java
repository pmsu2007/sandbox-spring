package com.mingxoop.sandbox.global.jwt;

import com.mingxoop.sandbox.domain.user.repository.entity.Role;
import com.mingxoop.sandbox.global.api.AppHttpStatus;
import com.mingxoop.sandbox.global.properties.JwtProperties;
import com.mingxoop.sandbox.global.security.AppUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtRepository {

	private final String BEARER_PREFIX = "Bearer";
	private final String AUTHORIZATION_HEADER = "Authorization";
	private final String REFRESH_HEADER = "Refresh";
	private final JwtProperties jwtProperties;
	private SecretKey key;

	@PostConstruct
	public void init() {
		String base64EncodedSecretKey = encodeBase64(jwtProperties.getSecretKey());
		this.key = getSecretKeyFromBase64EncodedKey(base64EncodedSecretKey);
	}

	private String encodeBase64(String target) {
		return Encoders.BASE64.encode(target.getBytes(StandardCharsets.UTF_8));
	}

	// HS256 (HMAC with SHA-256)
	private SecretKey getSecretKeyFromBase64EncodedKey(String key) {
		byte[] keyBytes = Decoders.BASE64.decode(key);
		return new SecretKeySpec(keyBytes, Jwts.SIG.HS256.key().build().getAlgorithm());
	}

	// 액세스 토큰, 리프레쉬 토큰 생성
	public TokenResponse generateToken(Long id, String email, Role role) {
		Date issuedAt = new Date(System.currentTimeMillis());
		Date accessTokenExpiration = getTokenExpiration(issuedAt, jwtProperties.getAccessExpirationMillis());
		Date refreshTokenExpiration = getTokenExpiration(issuedAt, jwtProperties.getRefreshExpirationMillis());

		String accessToken = Jwts.builder()
			.claims(generatePublicClaims(id, role))
			.subject(email)
			.expiration(accessTokenExpiration)
			.issuedAt(issuedAt)
			.signWith(key)
			.compact();

		String refreshToken = Jwts.builder()
			.subject(email)
			.expiration(refreshTokenExpiration)
			.issuedAt(issuedAt)
			.signWith(key)
			.compact();

		return TokenResponse.builder()
			.accessToken(accessToken)
			.accessTokenExpiration(toLocalDateTime(accessTokenExpiration))
			.refreshToken(refreshToken)
			.refreshTokenExpiration(toLocalDateTime(refreshTokenExpiration))
			.build();
	}

	private Date getTokenExpiration(Date issuedAt, long expirationMillis) {
		return new Date(issuedAt.getTime() + expirationMillis);
	}

	private LocalDateTime toLocalDateTime(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	// 공개 클레임
	private Map<String, String> generatePublicClaims(Long id, Role role) {
		Map<String, String> claims = new HashMap<>();
		claims.put("id", String.valueOf(id));
		claims.put("role", role.name());
		return claims;
	}

	// JWT 토큰 복호화 및 검증
	public Claims parseToken(String token) {
		try {
			return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
		} catch (JwtException e) {
			throw new JwtException(AppHttpStatus.INVALID_TOKEN.getMessage());
		}
	}

	// 액세스 토큰으로 Authentication 객체 가져오기
	public Authentication getAuthentication(String accessToken) {
		Claims claims = parseToken(accessToken);
		String id = claims.get("id").toString();
		String role = claims.get("role").toString();

		AppUserDetails user = AppUserDetails.of(
				Long.valueOf(id),
				claims.getSubject(),
				role
		);

		return new UsernamePasswordAuthenticationToken(
			user,
			null,
			user.getAuthorities()
		);
	}

	public String resolveAccessToken(HttpServletRequest request) {
        // 쿠키가 존재하는지 확인
        if (request.getCookies() == null) {
            return null;
        }

        // 쿠키 중 AUTHORIZATION_HEADER Key를 갖는 쿠키를 찾고 Value 가져오기
        String accessToken = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(AUTHORIZATION_HEADER))
                .findAny()
                .map(Cookie::getValue)
                .orElse(null);

        if (!StringUtils.hasText(accessToken) || !accessToken.startsWith(BEARER_PREFIX)) {
			return null;
		}
		return accessToken.substring(BEARER_PREFIX.length());
	}

	public String resolveRefreshToken(HttpServletRequest request) {
        // 쿠키가 존재하는지 확인
        if (request.getCookies() == null) {
            return null;
        }

        // 쿠키 중 REFRESH_HEADER Key를 갖는 쿠키를 찾고 Value 가져오기
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(REFRESH_HEADER))
                .findAny()
                .map(Cookie::getValue)
                .orElse(null);

		if (!StringUtils.hasText(refreshToken)) {
			return null;
		}

		return refreshToken;
	}
}