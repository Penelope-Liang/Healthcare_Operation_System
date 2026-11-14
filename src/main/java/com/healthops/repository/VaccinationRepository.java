package com.healthops.repository;

import com.healthops.domain.Vaccination;
import com.healthops.domain.VaccinationDetail;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class VaccinationRepository {

    private static final String COLUMNS = "OHIP, ClinicName, Lots, Date, Time";

    private final JdbcClient jdbc;

    public VaccinationRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<Vaccination> findAll() {
        return jdbc.sql("SELECT " + COLUMNS + " FROM Vaccination ORDER BY Date, Time").query(Rows::vaccination).list();
    }

    public Optional<Vaccination> findById(String ohip) {
        return jdbc.sql("SELECT " + COLUMNS + " FROM Vaccination WHERE OHIP = ?")
                .param(ohip).query(Rows::vaccination).optional();
    }

    /** Vaccinations with patient and manufacturer names, newest first. */
    public List<VaccinationDetail> findDetails(int limit) {
        return jdbc.sql("""
                SELECT Vaccination.OHIP, Patient.FirstName, Patient.LastName, Vaccination.ClinicName,
                       Vaccination.Lots, Vaccine.CompanyName, Vaccination.Date, Vaccination.Time
                FROM Vaccination
                JOIN Patient ON Patient.OHIP = Vaccination.OHIP
                LEFT JOIN Vaccine ON Vaccine.Lot = Vaccination.Lots
                ORDER BY Vaccination.Date DESC, Vaccination.Time DESC
                LIMIT ?
                """)
                .param(limit)
                .query((rs, n) -> new VaccinationDetail(rs.getString("OHIP"), rs.getString("FirstName"),
                        rs.getString("LastName"), rs.getString("ClinicName"), rs.getString("Lots"),
                        rs.getString("CompanyName"), rs.getString("Date"), rs.getString("Time")))
                .list();
    }

    public void insert(Vaccination v) {
        jdbc.sql("INSERT INTO Vaccination (" + COLUMNS + ") VALUES (?, ?, ?, ?, ?)")
                .params(v.ohip(), v.clinicName(), v.lot(), v.date(), v.time())
                .update();
    }

    public boolean update(Vaccination v) {
        return jdbc.sql("UPDATE Vaccination SET ClinicName = ?, Lots = ?, Date = ?, Time = ? WHERE OHIP = ?")
                .params(v.clinicName(), v.lot(), v.date(), v.time(), v.ohip())
                .update() > 0;
    }

    public boolean delete(String ohip) {
        return jdbc.sql("DELETE FROM Vaccination WHERE OHIP = ?").param(ohip).update() > 0;
    }
}
