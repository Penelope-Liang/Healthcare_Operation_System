package com.healthops.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

/**
 * Clinical worker roles. Nurses and doctors live in parallel table families
 * (Nurse/NurseCreds/NurseWork and Doctor/DoctorCreds/DoctorWork); each constant
 * carries its table and column names so SQL can be shared between them.
 */
public enum Role {
    NURSE("Nurse", "NurseCreds", "NurseWork", "NurseId", "NurseCredials"),
    DOCTOR("Doctor", "DoctorCreds", "DoctorWork", "DoctorId", "DoctorCredials");

    public final String workerTable;
    public final String credsTable;
    public final String workTable;
    public final String idColumn;
    public final String credentialColumn;

    Role(String workerTable, String credsTable, String workTable, String idColumn, String credentialColumn) {
        this.workerTable = workerTable;
        this.credsTable = credsTable;
        this.workTable = workTable;
        this.idColumn = idColumn;
        this.credentialColumn = credentialColumn;
    }

    @JsonValue
    public String key() {
        return name().toLowerCase(Locale.ROOT);
    }

    public String label() {
        return this == NURSE ? "Nurse" : "Doctor";
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
