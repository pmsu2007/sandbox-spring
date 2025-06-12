package com.mingxoop.sandbox.domain.user.controller;

import com.mingxoop.sandbox.domain.user.controller.request.UserCreate;
import com.mingxoop.sandbox.domain.user.service.AuthService;
import com.mingxoop.sandbox.global.api.response.BaseResponse;
import com.mingxoop.sandbox.global.api.response.PkResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<PkResponse>> signup(
            @RequestBody UserCreate userCreate
    ) {
        return ResponseEntity.ok(BaseResponse.success(
                authService.signup(
                        userCreate
                )
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<Void>> refreshToken(
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        authService.refreshToken(request, response);

        return ResponseEntity.ok(BaseResponse.success());
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authService.logout(request, response);

        return ResponseEntity.ok(BaseResponse.success());
    }
}
