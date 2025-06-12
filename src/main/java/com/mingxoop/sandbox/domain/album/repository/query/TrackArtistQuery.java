package com.mingxoop.sandbox.domain.album.repository.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TrackArtistQuery {

    private String trackId;
    private ArtistQuery artist;

    @QueryProjection
    public TrackArtistQuery(String trackId, ArtistQuery artist) {
        this.trackId = trackId;
        this.artist = artist;
    }
}
