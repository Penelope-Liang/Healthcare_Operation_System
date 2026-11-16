package com.healthops.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

/** Clinical staff roles, stored in staff.role as 'nurse' or 'doctor'. */
public enum Role {
    NURSE, DOCTOR;

    @JsonValue
    public String key() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static Role from(String value) {
        if (value != null) {
            for (Role role : values()) {
                if (role.key().equalsIgnoreCase(value.trim())) {
                    return role;
                }
            }
        }
        throw new IllegalArgumentException("Role must be nurse or doctor.");
    }
}
