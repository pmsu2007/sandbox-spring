package com.mingxoop.sandbox.domain.user.controller.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserLogin {
    private String email;
    private String password;

    @Builder
    public UserLogin(
            @JsonProperty("email") String email,
            @JsonProperty("password") String password
    ) {
        this.email = email;
        this.password = password;
    }
}
