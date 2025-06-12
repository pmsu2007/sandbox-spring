package com.mingxoop.sandbox.domain.album.repository.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ArtistQuery {
    private String id;
    private String name;

    @QueryProjection
    public ArtistQuery(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
