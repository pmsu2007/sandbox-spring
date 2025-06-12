package com.mingxoop.sandbox.domain.album.repository.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "ALBUMS")
@Getter
@NoArgsConstructor
public class AlbumEntity implements Persistable<String> {

    @Id
    @Column(updatable = false)
    private String id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AlbumType type;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "released_at")
    private LocalDateTime releasedAt;

    @OneToOne(mappedBy = "album", cascade = CascadeType.ALL)
    private AlbumReviewStatsEntity stats;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Builder
    public AlbumEntity(String id, String name, AlbumType type, String imageUrl, LocalDateTime releasedAt, AlbumReviewStatsEntity stats, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.imageUrl = imageUrl;
        this.releasedAt = releasedAt;
        this.stats = stats;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        this.modifiedAt = LocalDateTime.now();
    }

    @Override
    public boolean isNew() {
        return this.createdAt == null;
    }
}
