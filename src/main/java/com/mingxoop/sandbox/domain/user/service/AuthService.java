package com.mingxoop.sandbox.domain.user.service;

import com.mingxoop.sandbox.domain.user.controller.request.UserCreate;
import com.mingxoop.sandbox.global.api.PkResponse;

public interface AuthService {
    PkResponse signup(UserCreate userCreate);
}
