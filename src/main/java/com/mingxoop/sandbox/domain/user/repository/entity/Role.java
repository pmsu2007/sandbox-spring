package com.mingxoop.sandbox.domain.user.repository.entity;

public enum Role {
    USER, ADMIN;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
