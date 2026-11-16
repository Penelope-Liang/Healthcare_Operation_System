package com.healthops.repository;

import com.healthops.domain.Vaccination;
import com.healthops.domain.VaccinationDetail;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static com.healthops.repository.Columns.date;
import static com.healthops.repository.Columns.time;

@Repository
public class VaccinationRepository {

    private static final String SELECT = "SELECT id, patient_ohip, clinic_name, lot, given_on, given_at FROM vaccination";

    private final JdbcClient jdbc;

    public VaccinationRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<Vaccination> findAll() {
        return jdbc.sql(SELECT + " ORDER BY given_on, given_at, id").query(VaccinationRepository::map).list();
    }

    public Optional<Vaccination> findById(long id) {
        return jdbc.sql(SELECT + " WHERE id = ?").param(id).query(VaccinationRepository::map).optional();
    }

    /** Doses with patient and manufacturer names, newest first, numbered per patient in date order. */
    public List<VaccinationDetail> findDetails(int limit) {
        return jdbc.sql("""
                SELECT v.id, v.patient_ohip, p.first_name, p.last_name, v.clinic_name, v.lot, l.manufacturer,
                       v.given_on, v.given_at,
                       ROW_NUMBER() OVER (PARTITION BY v.patient_ohip ORDER BY v.given_on, v.given_at, v.id) AS dose_number
                FROM vaccination v
                JOIN patient p ON p.ohip = v.patient_ohip
                JOIN vaccine_lot l ON l.lot = v.lot
                ORDER BY v.given_on DESC, v.given_at DESC, v.id DESC
                LIMIT ?
                """)
                .param(limit)
                .query((rs, n) -> new VaccinationDetail(rs.getLong("id"), rs.getString("patient_ohip"),
                        rs.getString("first_name"), rs.getString("last_name"), rs.getString("clinic_name"),
                        rs.getString("lot"), rs.getString("manufacturer"), date(rs, "given_on"),
                        time(rs, "given_at"), rs.getInt("dose_number")))
                .list();
    }

    public Vaccination insert(Vaccination v) {
        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.sql("INSERT INTO vaccination (patient_ohip, clinic_name, lot, given_on, given_at) VALUES (?, ?, ?, ?, ?)")
                .params(v.ohip(), v.clinicName(), v.lot(), v.date(), v.time())
                .update(keys, "id");
        return v.withId(keys.getKey().longValue());
    }

    public boolean update(Vaccination v) {
        return jdbc.sql("UPDATE vaccination SET patient_ohip = ?, clinic_name = ?, lot = ?, given_on = ?, given_at = ? WHERE id = ?")
                .params(v.ohip(), v.clinicName(), v.lot(), v.date(), v.time(), v.id())
                .update() > 0;
    }

    public boolean delete(long id) {
        return jdbc.sql("DELETE FROM vaccination WHERE id = ?").param(id).update() > 0;
    }

    private static Vaccination map(ResultSet rs, int rowNum) throws SQLException {
        return new Vaccination(rs.getLong("id"), rs.getString("patient_ohip"), rs.getString("clinic_name"),
                rs.getString("lot"), date(rs, "given_on"), time(rs, "given_at"));
    }
}
