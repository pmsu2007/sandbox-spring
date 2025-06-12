package com.mingxoop.sandbox.domain.album.repository.query;

import com.mingxoop.sandbox.domain.album.repository.entity.AlbumType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class AlbumQuery {
    private String id;
    private String name;
    private AlbumType type;
    private String imageUrl;
    private LocalDateTime releasedAt;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private AlbumReviewStatsQuery stats;
    @Setter
    private List<TrackQuery> tracks;
    @Setter
    private List<ArtistQuery> artists;

    @QueryProjection
    public AlbumQuery(String id, String name, AlbumType type, String imageUrl, LocalDateTime releasedAt, LocalDateTime createdAt, LocalDateTime modifiedAt, AlbumReviewStatsQuery stats) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.imageUrl = imageUrl;
        this.releasedAt = releasedAt;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.stats = stats;
    }
}
