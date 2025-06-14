package com.mingxoop.sandbox.domain.test;

import com.mingxoop.sandbox.global.api.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestController {

    @GetMapping("")
    public ResponseEntity<BaseResponse<String>> versionCheck() {
        return ResponseEntity.ok(BaseResponse.success("v1"));
    }
}
