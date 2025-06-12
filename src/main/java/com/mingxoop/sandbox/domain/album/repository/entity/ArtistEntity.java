package com.mingxoop.sandbox.domain.album.repository.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;

@Entity
@Table(name = "ARTISTS")
@Getter
@NoArgsConstructor
public class ArtistEntity implements Persistable<String> {

	@Id
	@Column(updatable = false, length = 22)
	private String id;

	@Column(name = "name")
	private String name;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "modified_at")
	private LocalDateTime modifiedAt;

    @Builder
    public ArtistEntity(String id, String name) {
        this.id = id;
        this.name = name;
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
