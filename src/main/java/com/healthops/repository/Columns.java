package com.healthops.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

/** Small helpers for reading SQL DATE/TIME columns as java.time values. */
final class Columns {

    private Columns() {
    }

    static LocalDate date(ResultSet rs, String column) throws SQLException {
        return rs.getObject(column, LocalDate.class);
    }

    static LocalTime time(ResultSet rs, String column) throws SQLException {
        return rs.getObject(column, LocalTime.class);
    }
}
