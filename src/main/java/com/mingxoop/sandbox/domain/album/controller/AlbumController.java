package com.mingxoop.sandbox.domain.album.controller;

import com.mingxoop.sandbox.domain.album.controller.response.AlbumListResponse;
import com.mingxoop.sandbox.domain.album.service.AlbumService;
import com.mingxoop.sandbox.global.api.response.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/albums")
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping("")
    public ResponseEntity<CursorResponse<AlbumListResponse>> getAlbumsByReviewCount(
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "16") long limit
    ) {
        return ResponseEntity.ok(albumService.getAlbumsByReviewCount(
                cursor,
                limit
        ));
    }
}
