package com.healthops.domain;

import java.time.LocalDate;

/** A registered patient. {@code spouseOhip} comes from the spouse table and may be null. */
public record Patient(String ohip, String firstName, String lastName, LocalDate dateOfBirth, String spouseOhip) {
}
