package com.healthops.domain;

public record Patient(String ohip, String spouseOhip, String firstName, String lastName, String dob) {

    public String fullName() {
        return firstName + " " + lastName;
    }
}
