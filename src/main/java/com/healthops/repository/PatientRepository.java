package com.healthops.repository;

import com.healthops.domain.Patient;
import com.healthops.domain.PatientOverview;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PatientRepository {

    // Legacy seed data stores a missing spouse as the string 'NULL'; normalise it on read.
    private static final String COLUMNS =
            "Patient.OHIP, NULLIF(Patient.SpouseOHIP, 'NULL') AS SpouseOHIP, Patient.FirstName, Patient.LastName, Patient.DOB";

    private final JdbcClient jdbc;

    public PatientRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<Patient> findAll() {
        return jdbc.sql("SELECT " + COLUMNS + " FROM Patient ORDER BY LastName, FirstName")
                .query(Rows::patient).list();
    }

    public Optional<Patient> findById(String ohip) {
        return jdbc.sql("SELECT " + COLUMNS + " FROM Patient WHERE OHIP = ?")
                .param(ohip).query(Rows::patient).optional();
    }

    public List<PatientOverview> findOverview() {
        return jdbc.sql("""
                SELECT %s, Vaccination.ClinicName, Vaccination.Lots, Vaccine.CompanyName,
                       Vaccination.Date, Vaccination.Time
                FROM Patient
                LEFT JOIN Vaccination ON Vaccination.OHIP = Patient.OHIP
                LEFT JOIN Vaccine ON Vaccine.Lot = Vaccination.Lots
                ORDER BY Patient.LastName, Patient.FirstName
                """.formatted(COLUMNS))
                .query((rs, n) -> new PatientOverview(
                        rs.getString("OHIP"), rs.getString("SpouseOHIP"),
                        rs.getString("FirstName"), rs.getString("LastName"), rs.getString("DOB"),
                        rs.getString("ClinicName"), rs.getString("Lots"), rs.getString("CompanyName"),
                        rs.getString("Date"), rs.getString("Time")))
                .list();
    }

    /** Patients without a vaccination record (the schema allows one record per patient). */
    public List<Patient> findUnvaccinated() {
        return jdbc.sql("SELECT " + COLUMNS + " FROM Patient WHERE OHIP NOT IN (SELECT OHIP FROM Vaccination) "
                        + "ORDER BY LastName, FirstName")
                .query(Rows::patient).list();
    }

    public void insert(Patient p) {
        jdbc.sql("INSERT INTO Patient (OHIP, SpouseOHIP, FirstName, LastName, DOB) VALUES (?, ?, ?, ?, ?)")
                .params(p.ohip(), p.spouseOhip(), p.firstName(), p.lastName(), p.dob())
                .update();
    }

    public boolean update(Patient p) {
        return jdbc.sql("UPDATE Patient SET SpouseOHIP = ?, FirstName = ?, LastName = ?, DOB = ? WHERE OHIP = ?")
                .params(p.spouseOhip(), p.firstName(), p.lastName(), p.dob(), p.ohip())
                .update() > 0;
    }

    public boolean delete(String ohip) {
        return jdbc.sql("DELETE FROM Patient WHERE OHIP = ?").param(ohip).update() > 0;
    }
}
