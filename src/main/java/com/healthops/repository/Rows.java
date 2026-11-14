package com.healthops.repository;

import com.healthops.domain.Clinic;
import com.healthops.domain.Patient;
import com.healthops.domain.Vaccination;
import com.healthops.domain.Vaccine;

import java.sql.ResultSet;
import java.sql.SQLException;

/** Row mappers shared by the repositories. Column names follow database/covidDB.sql. */
final class Rows {

    private Rows() {
    }

    static Patient patient(ResultSet rs, int rowNum) throws SQLException {
        return new Patient(rs.getString("OHIP"), rs.getString("SpouseOHIP"),
                rs.getString("FirstName"), rs.getString("LastName"), rs.getString("DOB"));
    }

    static Clinic clinic(ResultSet rs, int rowNum) throws SQLException {
        return new Clinic(rs.getString("Name"), rs.getString("Street"), rs.getString("City"),
                rs.getString("Prov"), rs.getString("PC"), rs.getString("date"));
    }

    static Vaccine vaccine(ResultSet rs, int rowNum) throws SQLException {
        return new Vaccine(rs.getString("Lot"), rs.getString("CompanyName"), rs.getString("Prodcution"),
                rs.getString("Expiry"), rs.getInt("Doses"));
    }

    static Vaccination vaccination(ResultSet rs, int rowNum) throws SQLException {
        return new Vaccination(rs.getString("OHIP"), rs.getString("ClinicName"), rs.getString("Lots"),
                rs.getString("Date"), rs.getString("Time"));
    }
}
