package com.healthops.domain;

/** A patient joined with their (optional) vaccination record, for the patient directory. */
public record PatientOverview(
        String ohip,
        String spouseOhip,
        String firstName,
        String lastName,
        String dob,
        String clinicName,
        String lot,
        String manufacturer,
        String vaccinationDate,
        String vaccinationTime) {

    public String fullName() {
        return firstName + " " + lastName;
    }

    public boolean vaccinated() {
        return vaccinationDate != null;
    }
}
