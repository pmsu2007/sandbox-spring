package com.mingxoop.sandbox.domain.album.repository.jpa;

import com.mingxoop.sandbox.domain.album.repository.entity.AlbumEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumJpaRepository extends JpaRepository<AlbumEntity, String> {
}
