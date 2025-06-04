package com.mingxoop.sandbox.domain.user.repository.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "access_token_blacklists"
)
@Getter
@NoArgsConstructor
public class AccessTokenBlacklistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "jti", nullable = false, length = 255)
    private String jti;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Builder
    public AccessTokenBlacklistEntity(Long id, String jti, LocalDateTime expiresAt, LocalDateTime createdAt, UserEntity user) {
        this.id = id;
        this.jti = jti;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.user = user;
    }

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

}
