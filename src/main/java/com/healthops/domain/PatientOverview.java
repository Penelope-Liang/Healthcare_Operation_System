package com.healthops.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;

/** A patient with their dose count and most recent vaccination, for the patient directory. */
public record PatientOverview(
        String ohip,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String spouseOhip,
        int doses,
        LocalDate lastDoseDate,
        @JsonFormat(pattern = "HH:mm") LocalTime lastDoseTime,
        String lastClinicName,
        String lastLot,
        String lastManufacturer) {
}
