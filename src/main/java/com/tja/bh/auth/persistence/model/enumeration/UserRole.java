package com.tja.bh.auth.persistence.model.enumeration;

import com.tja.bh.auth.persistence.model.Role;
import lombok.Getter;

@Getter
public enum UserRole {
    USER(1L, "ROLE_USER"),
    ADMIN(0L, "ROLE_ADMIN");

    private String name;
    private Long id;

    UserRole(Long id, String name) {
        this.id = id;
        this.name = name;
    }


    public Role convertToRole() {
        return new Role(id, name);
    }
}
