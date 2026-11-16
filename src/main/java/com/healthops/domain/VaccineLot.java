package com.healthops.domain;

import java.time.LocalDate;

public record VaccineLot(String lot, String manufacturer, LocalDate productionDate, LocalDate expiryDate, int doses) {
}
