package com.mingxoop.sandbox.domain.album.service;

import com.mingxoop.sandbox.domain.album.controller.response.AlbumListResponse;
import com.mingxoop.sandbox.global.api.response.CursorResponse;
import com.mingxoop.sandbox.global.api.response.OffsetResponse;

public interface AlbumService {
    CursorResponse<AlbumListResponse> getAlbumsByReviewCountWithCursor(String encodedCursor, long limit);
    OffsetResponse<AlbumListResponse> getAlbumsByReviewCountWithOffset(long offset, long limit);
}
