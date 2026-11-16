package com.healthops.domain;

import java.time.LocalDate;

public record Clinic(String name, String street, String city, String province, String postalCode,
                     LocalDate operatingDate) {
}
