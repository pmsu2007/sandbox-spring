package com.mingxoop.sandbox.domain.album.repository.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;

@Entity
@Table(name = "TRACKS")
@Getter
@NoArgsConstructor
public class TrackEntity implements Persistable<String> {

	@Id
	@Column(updatable = false, length = 22)
	private String id;

	@Column(name = "name")
	private String name;

	@Column(name = "track_number", nullable = false)
	private Integer trackNumber;

	@Column(name = "duration")
	private Long duration = 0L;

	@Column(name = "is_playable")
	private Boolean isPlayable = false;

	@Column(name = "preview_url")
	private String previewUrl;

	@JoinColumn(name = "album_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private AlbumEntity album;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "modified_at")
	private LocalDateTime modifiedAt;

	@Builder
	public TrackEntity(String id, String name, Integer trackNumber, Long duration, Boolean isPlayable, String previewUrl, AlbumEntity album, LocalDateTime createdAt, LocalDateTime modifiedAt) {
		this.id = id;
		this.name = name;
		this.trackNumber = trackNumber;
		this.duration = duration;
		this.isPlayable = isPlayable;
		this.previewUrl = previewUrl;
		this.album = album;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
	}

	@PrePersist
	private void prePersist() {
		this.createdAt = LocalDateTime.now();
	}

	@PreUpdate
	private void preUpdate() {
		this.modifiedAt = LocalDateTime.now();
	}

	@Override
    public boolean isNew() {
        return this.createdAt == null;
    }
}
