package com.mingxoop.sandbox.domain.album.repository.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "ALBUM_REVIEW_STATS"
)
@Getter
@NoArgsConstructor
public class AlbumReviewStatsEntity {

    @Id
    private String id;

    @Column(name = "total_reviewer")
    private Long totalReviewer = 0L;

    @Column(name = "total_score")
    private Long totalScore = 0L;

    @Column(name = "average_score")
    private Double averageScore = 0.0;

    @Column(name = "score_1_count")
    private Long score1Count = 0L;

    @Column(name = "score_2_count")
    private Long score2Count = 0L;

    @Column(name = "score_3_count")
    private Long score3Count = 0L;

    @Column(name = "score_4_count")
    private Long score4Count = 0L;

    @Column(name = "score_5_count")
    private Long score5Count = 0L;

    @Column(name = "male_count")
    private Long maleCount = 0L;

    @Column(name = "male_total_score")
    private Long maleTotalScore = 0L;

    @Column(name = "female_count")
    private Long femaleCount = 0L;

    @Column(name = "female_total_score")
    private Long femaleTotalScore = 0L;

    @MapsId
    @JoinColumn(name = "album_id")
    @OneToOne(fetch = FetchType.LAZY)
    private AlbumEntity album;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Builder
    public AlbumReviewStatsEntity(String id, Long totalReviewer, Long totalScore, Double averageScore, Long score1Count, Long score2Count, Long score3Count, Long score4Count, Long score5Count, Long maleCount, Long maleTotalScore, Long femaleCount, Long femaleTotalScore, AlbumEntity album, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.totalReviewer = totalReviewer;
        this.totalScore = totalScore;
        this.averageScore = averageScore;
        this.score1Count = score1Count;
        this.score2Count = score2Count;
        this.score3Count = score3Count;
        this.score4Count = score4Count;
        this.score5Count = score5Count;
        this.maleCount = maleCount;
        this.maleTotalScore = maleTotalScore;
        this.femaleCount = femaleCount;
        this.femaleTotalScore = femaleTotalScore;
        this.album = album;
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
}
