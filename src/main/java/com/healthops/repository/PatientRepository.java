package com.healthops.repository;

import com.healthops.domain.Patient;
import com.healthops.domain.PatientOverview;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static com.healthops.repository.Columns.date;
import static com.healthops.repository.Columns.time;

@Repository
public class PatientRepository {

    private static final String SELECT = """
            SELECT p.ohip, p.first_name, p.last_name, p.date_of_birth, s.ohip AS spouse_ohip
            FROM patient p
            LEFT JOIN spouse s ON s.patient_ohip = p.ohip
            """;

    private final JdbcClient jdbc;

    public PatientRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<Patient> findAll() {
        return jdbc.sql(SELECT + " ORDER BY p.last_name, p.first_name").query(PatientRepository::map).list();
    }

    public Optional<Patient> findById(String ohip) {
        return jdbc.sql(SELECT + " WHERE p.ohip = ?").param(ohip).query(PatientRepository::map).optional();
    }

    /** Every patient with their number of doses and their most recent dose (if any). */
    public List<PatientOverview> findOverview() {
        return jdbc.sql("""
                SELECT p.ohip, p.first_name, p.last_name, p.date_of_birth, s.ohip AS spouse_ohip,
                       COALESCE(last_dose.doses, 0) AS doses, last_dose.given_on, last_dose.given_at,
                       last_dose.clinic_name, last_dose.lot, last_dose.manufacturer
                FROM patient p
                LEFT JOIN spouse s ON s.patient_ohip = p.ohip
                LEFT JOIN (
                    SELECT v.patient_ohip, v.given_on, v.given_at, v.clinic_name, v.lot, l.manufacturer,
                           COUNT(*) OVER (PARTITION BY v.patient_ohip) AS doses,
                           ROW_NUMBER() OVER (PARTITION BY v.patient_ohip
                                              ORDER BY v.given_on DESC, v.given_at DESC, v.id DESC) AS rn
                    FROM vaccination v
                    JOIN vaccine_lot l ON l.lot = v.lot
                ) last_dose ON last_dose.patient_ohip = p.ohip AND last_dose.rn = 1
                ORDER BY p.last_name, p.first_name
                """)
                .query((rs, n) -> new PatientOverview(
                        rs.getString("ohip"), rs.getString("first_name"), rs.getString("last_name"),
                        date(rs, "date_of_birth"), rs.getString("spouse_ohip"), rs.getInt("doses"),
                        date(rs, "given_on"), time(rs, "given_at"), rs.getString("clinic_name"),
                        rs.getString("lot"), rs.getString("manufacturer")))
                .list();
    }

    @Transactional
    public void insert(Patient p) {
        jdbc.sql("INSERT INTO patient (ohip, first_name, last_name, date_of_birth) VALUES (?, ?, ?, ?)")
                .params(p.ohip(), p.firstName(), p.lastName(), p.dateOfBirth())
                .update();
        saveSpouse(p);
    }

    @Transactional
    public boolean update(Patient p) {
        int updated = jdbc.sql("UPDATE patient SET first_name = ?, last_name = ?, date_of_birth = ? WHERE ohip = ?")
                .params(p.firstName(), p.lastName(), p.dateOfBirth(), p.ohip())
                .update();
        if (updated == 0) {
            return false;
        }
        saveSpouse(p);
        return true;
    }

    /** Spouse rows are deleted with the patient (ON DELETE CASCADE); vaccinations block the delete. */
    public boolean delete(String ohip) {
        return jdbc.sql("DELETE FROM patient WHERE ohip = ?").param(ohip).update() > 0;
    }

    /**
     * Keeps the spouse table in line with the patient's spouse OHIP. Contact details are kept
     * when the OHIP is unchanged and cleared when a different spouse is recorded.
     */
    private void saveSpouse(Patient p) {
        if (p.spouseOhip() == null) {
            jdbc.sql("DELETE FROM spouse WHERE patient_ohip = ?").param(p.ohip()).update();
            return;
        }
        int same = jdbc.sql("SELECT COUNT(*) FROM spouse WHERE patient_ohip = ? AND ohip = ?")
                .params(p.ohip(), p.spouseOhip()).query(Integer.class).single();
        if (same == 0) {
            jdbc.sql("DELETE FROM spouse WHERE patient_ohip = ?").param(p.ohip()).update();
            jdbc.sql("INSERT INTO spouse (patient_ohip, ohip) VALUES (?, ?)").params(p.ohip(), p.spouseOhip()).update();
        }
    }

    private static Patient map(ResultSet rs, int rowNum) throws SQLException {
        return new Patient(rs.getString("ohip"), rs.getString("first_name"), rs.getString("last_name"),
                date(rs, "date_of_birth"), rs.getString("spouse_ohip"));
    }
}
