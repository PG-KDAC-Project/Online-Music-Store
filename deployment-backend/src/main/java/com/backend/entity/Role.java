package com.backend.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN,
    ARTIST,
    LISTENER;

    @Override
    public String getAuthority() {
        return name();
    }
}
