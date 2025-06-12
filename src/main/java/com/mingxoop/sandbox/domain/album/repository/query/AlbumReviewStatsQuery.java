package com.mingxoop.sandbox.domain.album.repository.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AlbumReviewStatsQuery {
    private String id;

    private long totalReviewer;

    private long totalScore;

    private double averageScore;

    private long score1Count;

    private long score2Count;

    private long score3Count;

    private long score4Count;

    private long score5Count;

    private long maleCount;

    private long maleTotalScore;

    private long femaleCount;

    private long femaleTotalScore;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @QueryProjection
    public AlbumReviewStatsQuery(String id, long totalReviewer, long totalScore, double averageScore, long score1Count, long score2Count, long score3Count, long score4Count, long score5Count, long maleCount, long maleTotalScore, long femaleCount, long femaleTotalScore, LocalDateTime createdAt, LocalDateTime modifiedAt) {
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
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
