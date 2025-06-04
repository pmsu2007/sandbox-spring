package com.mingxoop.sandbox.global.jwt;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private LocalDateTime accessTokenExpiration;
    private String refreshToken;
    private LocalDateTime refreshTokenExpiration;
}
