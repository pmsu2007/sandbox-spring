package com.mingxoop.sandbox.domain.album.repository.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AlbumArtistQuery {
    private String albumId;
    private ArtistQuery artist;

    @QueryProjection
    public AlbumArtistQuery(String albumId, ArtistQuery artist) {
        this.albumId = albumId;
        this.artist = artist;
    }
}
