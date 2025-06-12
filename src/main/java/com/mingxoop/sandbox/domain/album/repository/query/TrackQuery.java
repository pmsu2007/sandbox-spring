package com.mingxoop.sandbox.domain.album.repository.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class TrackQuery {

    private String id;
    private String name;
    private int trackNumber;
    private long duration;
    private boolean isPlayable;
    private String previewUrl;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    @Setter
    private List<ArtistQuery> artists;

    @QueryProjection
    public TrackQuery(String id, String name, int trackNumber, long duration, boolean isPlayable, String previewUrl, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.name = name;
        this.trackNumber = trackNumber;
        this.duration = duration;
        this.isPlayable = isPlayable;
        this.previewUrl = previewUrl;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
