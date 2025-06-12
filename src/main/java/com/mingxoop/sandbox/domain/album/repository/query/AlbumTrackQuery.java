package com.mingxoop.sandbox.domain.album.repository.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AlbumTrackQuery {

    private String albumId;
    private TrackQuery track;

    @QueryProjection
    public AlbumTrackQuery(String albumId, TrackQuery track) {
        this.albumId = albumId;
        this.track = track;
    }
}
