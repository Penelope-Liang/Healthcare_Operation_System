package com.healthops.web.form;

/** Shared validation patterns. Lengths mirror the column sizes in database/covidDB.sql. */
public final class Patterns {

    public static final String OHIP = "[0-9]{4}-[0-9]{3}-[0-9]{3}";
    /** ShipTo.Lots and Vaccination.Lots are CHAR(6), so a lot code longer than 6 could never be shipped. */
    public static final String LOT = "[A-Za-z0-9]{1,6}";
    public static final String WORKER_ID = "[A-Za-z0-9]{1,5}";
    public static final String WORKER_NAME = "[A-Za-z]{1,15}";

    private Patterns() {
    }
}
