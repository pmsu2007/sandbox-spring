package com.mingxoop.sandbox.domain.album.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mingxoop.sandbox.domain.album.repository.query.AlbumReviewStatsQuery;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
public class AlbumReviewStatsSimpleResponse {
    private String id;
    private long count;
    private double averageScore;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime modifiedAt;

    public static AlbumReviewStatsSimpleResponse from(AlbumReviewStatsQuery stats) {
        return AlbumReviewStatsSimpleResponse.builder()
                .id(stats.getId())
                .count(stats.getTotalReviewer())
                .averageScore(0.0)
                .createdAt(stats.getCreatedAt())
                .modifiedAt(stats.getModifiedAt())
                .build();
    }
}
