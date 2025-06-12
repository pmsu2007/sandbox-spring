package com.mingxoop.sandbox.domain.user.service;

import com.mingxoop.sandbox.domain.user.controller.request.UserCreate;
import com.mingxoop.sandbox.global.api.response.PkResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    PkResponse signup(UserCreate userCreate);
    void refreshToken(HttpServletRequest request, HttpServletResponse response);
    void logout(HttpServletRequest request, HttpServletResponse response);
}
