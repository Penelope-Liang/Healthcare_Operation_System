package com.healthops.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;

/** One dose given to a patient. */
public record Vaccination(Long id, String ohip, String clinicName, String lot, LocalDate date,
                          @JsonFormat(pattern = "HH:mm") LocalTime time) {

    public Vaccination withId(long newId) {
        return new Vaccination(newId, ohip, clinicName, lot, date, time);
    }
}
