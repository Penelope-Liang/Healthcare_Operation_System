package com.healthops.domain;

/** A vaccination record joined with patient and manufacturer names, for display. */
public record VaccinationDetail(
        String ohip,
        String firstName,
        String lastName,
        String clinicName,
        String lot,
        String manufacturer,
        String date,
        String time) {

    public String patientName() {
        return firstName + " " + lastName;
    }
}
