package com.healthops.repository;

import com.healthops.domain.Clinic;
import com.healthops.domain.ClinicOverview;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.healthops.repository.Columns.date;

@Repository
public class ClinicRepository {

    private static final String SELECT =
            "SELECT name, street, city, province, postal_code, operating_date FROM clinic";

    private final JdbcClient jdbc;

    public ClinicRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<Clinic> findAll() {
        return jdbc.sql(SELECT + " ORDER BY name").query(ClinicRepository::map).list();
    }

    public Optional<Clinic> findById(String name) {
        return jdbc.sql(SELECT + " WHERE name = ?").param(name).query(ClinicRepository::map).optional();
    }

    /** Every clinic with its shipped lots, staff counts by role and number of doses given. */
    public List<ClinicOverview> findOverview() {
        Map<String, List<String>> lots = new HashMap<>();
        jdbc.sql("SELECT clinic_name, lot FROM shipment ORDER BY lot").query(rs -> {
            lots.computeIfAbsent(rs.getString("clinic_name"), k -> new ArrayList<>()).add(rs.getString("lot"));
        });
        Map<String, Integer> nurses = new HashMap<>();
        Map<String, Integer> doctors = new HashMap<>();
        jdbc.sql("""
                SELECT a.clinic_name, s.role, COUNT(*) AS c
                FROM staff_assignment a JOIN staff s ON s.id = a.staff_id
                GROUP BY a.clinic_name, s.role
                """).query(rs -> {
            ("nurse".equals(rs.getString("role")) ? nurses : doctors).put(rs.getString("clinic_name"), rs.getInt("c"));
        });
        Map<String, Integer> doses = new HashMap<>();
        jdbc.sql("SELECT clinic_name, COUNT(*) AS c FROM vaccination GROUP BY clinic_name").query(rs -> {
            doses.put(rs.getString("clinic_name"), rs.getInt("c"));
        });

        return findAll().stream()
                .map(c -> new ClinicOverview(c, lots.getOrDefault(c.name(), List.of()),
                        nurses.getOrDefault(c.name(), 0), doctors.getOrDefault(c.name(), 0),
                        doses.getOrDefault(c.name(), 0)))
                .toList();
    }

    public void insert(Clinic c) {
        jdbc.sql("INSERT INTO clinic (name, street, city, province, postal_code, operating_date) VALUES (?, ?, ?, ?, ?, ?)")
                .params(c.name(), c.street(), c.city(), c.province(), c.postalCode(), c.operatingDate())
                .update();
    }

    public boolean update(Clinic c) {
        return jdbc.sql("UPDATE clinic SET street = ?, city = ?, province = ?, postal_code = ?, operating_date = ? WHERE name = ?")
                .params(c.street(), c.city(), c.province(), c.postalCode(), c.operatingDate(), c.name())
                .update() > 0;
    }

    /** Shipments and staff assignments cascade; vaccinations given here block the delete. */
    public boolean delete(String name) {
        return jdbc.sql("DELETE FROM clinic WHERE name = ?").param(name).update() > 0;
    }

    private static Clinic map(ResultSet rs, int rowNum) throws SQLException {
        return new Clinic(rs.getString("name"), rs.getString("street"), rs.getString("city"),
                rs.getString("province"), rs.getString("postal_code"), date(rs, "operating_date"));
    }
}
