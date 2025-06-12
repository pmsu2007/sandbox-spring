package com.mingxoop.sandbox.domain.album.controller.response;

import com.mingxoop.sandbox.domain.album.repository.query.ArtistQuery;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ArtistResponse {
	private String id;
	private String name;
	public static ArtistResponse from (ArtistQuery artist) {
		return ArtistResponse.builder()
			.id(artist.getId())
			.name(artist.getName())
			.build();
	}
}
