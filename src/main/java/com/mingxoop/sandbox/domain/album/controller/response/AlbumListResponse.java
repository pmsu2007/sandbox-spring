package com.mingxoop.sandbox.domain.album.controller.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.mingxoop.sandbox.domain.album.repository.query.AlbumQuery;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AlbumListResponse {
    private String id;
    private String name;
    private String imageUrl;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime releasedAt;
    private AlbumReviewStatsSimpleResponse stats;
    private List<ArtistResponse> artists;
    private List<TrackResponse> tracks;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime modifiedAt;

    public static AlbumListResponse from (AlbumQuery album) {

        return AlbumListResponse.builder()
                .id(album.getId())
                .name(album.getName())
                .imageUrl(album.getImageUrl())
                .releasedAt(album.getReleasedAt())
                .artists(album.getArtists().stream()
                        .map(ArtistResponse::from)
                        .toList())
                .tracks(album.getTracks().stream()
                        .map(TrackResponse::from)
                        .toList())
                .stats(AlbumReviewStatsSimpleResponse.from(album.getStats()))
                .modifiedAt(album.getModifiedAt())
                .createdAt(album.getCreatedAt())
                .build();
    }
}
