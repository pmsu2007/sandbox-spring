package com.mingxoop.sandbox.domain.album.service;

import com.mingxoop.sandbox.domain.album.controller.response.AlbumListResponse;
import com.mingxoop.sandbox.domain.album.repository.AlbumRepository;
import com.mingxoop.sandbox.domain.album.repository.query.AlbumQuery;
import com.mingxoop.sandbox.global.api.response.CursorResponse;
import com.mingxoop.sandbox.global.util.CursorUtil;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Builder
@RequiredArgsConstructor
@Transactional
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;

    @Override
    @Transactional(readOnly = true)
    public CursorResponse<AlbumListResponse> getAlbumsByReviewCount(String encodedCursor, long limit) {
        Map<String, Object> cursor = encodedCursor != null ? CursorUtil.decode(encodedCursor) : Map.of();
        String albumId = (String) cursor.getOrDefault("albumId", null);
        Long reviewCount = CursorUtil.getAsLong(cursor, "reviewCount");

        List<AlbumQuery> albums = albumRepository.findAllByReviewCountCursorPaging(albumId, reviewCount, limit + 1);

        AlbumQuery lastAlbum = albums.get(albums.size() == limit + 1 ? albums.size() - 2 : albums.size() - 1);
        String nextCursor = CursorUtil.encode(Map.of(
                "albumId", lastAlbum.getId(),
                "reviewCount", lastAlbum.getStats().getTotalReviewer()
        ));

        return CursorResponse.of(
                albums.stream().map(AlbumListResponse::from).toList(),
                nextCursor,
                limit
        );
    }
}
