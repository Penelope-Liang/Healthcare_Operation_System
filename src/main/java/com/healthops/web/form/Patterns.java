package com.healthops.web.form;

/** Shared validation patterns. Lengths mirror the column sizes in database/schema.sql. */
public final class Patterns {

    public static final String OHIP = "[0-9]{4}-[0-9]{3}-[0-9]{3}";
    /** Letters, plus spaces, hyphens and apostrophes after the first letter (e.g. "O'Neil", "Mary-Kate"). */
    public static final String NAME = "[A-Za-z][A-Za-z' -]{0,29}";
    public static final String LOT = "[A-Za-z0-9]{1,12}";
    public static final String STAFF_ID = "[A-Za-z0-9]{1,5}";

    public static final String NAME_MESSAGE = "must be 1-30 letters (spaces, hyphens and apostrophes allowed).";

    private Patterns() {
    }
}
