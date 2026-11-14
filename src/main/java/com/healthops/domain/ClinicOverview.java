package com.healthops.domain;

import java.util.List;

/** A clinic with the vaccine lots shipped to it and its staffing levels. */
public record ClinicOverview(Clinic clinic, List<String> lots, int nurses, int doctors, int vaccinations) {

    public int staff() {
        return nurses + doctors;
    }
}
