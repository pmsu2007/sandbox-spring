package com.mingxoop.sandbox.domain.album.repository;

import com.mingxoop.sandbox.domain.album.repository.query.AlbumQuery;

import java.util.List;

public interface AlbumRepository {
    List<AlbumQuery> findAllByReviewCountCursorPaging(String cursorId, Long cursorReviewCount, long limit);
}
