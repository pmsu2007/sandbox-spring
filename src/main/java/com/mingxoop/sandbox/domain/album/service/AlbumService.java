package com.mingxoop.sandbox.domain.album.service;

import com.mingxoop.sandbox.domain.album.controller.response.AlbumListResponse;
import com.mingxoop.sandbox.global.api.response.CursorResponse;

public interface AlbumService {
    CursorResponse<AlbumListResponse> getAlbumsByReviewCount(String encodedCursor, long limit);
}
