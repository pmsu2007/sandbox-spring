package com.mingxoop.sandbox.domain.album.controller.response;

import com.mingxoop.sandbox.domain.album.repository.query.TrackQuery;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class TrackResponse {
	private String id;
	private String name;
	private int trackNumber;
	private long duration;
	private boolean isPlayable;
	private String previewUrl;
    private List<ArtistResponse> artists;

	public static TrackResponse from (TrackQuery track) {
		return TrackResponse.builder()
			.id(track.getId())
			.name(track.getName())
			.trackNumber(track.getTrackNumber())
			.duration(track.getDuration())
			.isPlayable(track.isPlayable())
			.previewUrl(track.getPreviewUrl())
            .artists(track.getArtists().stream()
                    .map(ArtistResponse::from)
                    .toList())
			.build();
	}

}
