package com.healthops.domain;

/** A vaccine lot. {@code production} maps to the legacy "Prodcution" column. */
public record Vaccine(String lot, String companyName, String production, String expiry, int doses) {
}
