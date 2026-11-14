package com.healthops.domain;

public record Worker(Role role, String id, String firstName, String lastName, String credential, String clinicName) {

    public String fullName() {
        return firstName + " " + lastName;
    }
}
