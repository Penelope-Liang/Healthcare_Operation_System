package com.healthops.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;

/** A dose with patient and manufacturer names; {@code doseNumber} counts the patient's doses in date order. */
public record VaccinationDetail(
        long id,
        String ohip,
        String firstName,
        String lastName,
        String clinicName,
        String lot,
        String manufacturer,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime time,
        int doseNumber) {
}
