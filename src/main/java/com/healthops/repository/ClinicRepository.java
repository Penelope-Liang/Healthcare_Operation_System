package com.healthops.repository;

import com.healthops.domain.Clinic;
import com.healthops.domain.ClinicOverview;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ClinicRepository {

    private static final String COLUMNS = "Name, Street, City, Prov, PC, date";

    private final JdbcClient jdbc;

    public ClinicRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<Clinic> findAll() {
        return jdbc.sql("SELECT " + COLUMNS + " FROM VaxClinic ORDER BY Name").query(Rows::clinic).list();
    }

    public Optional<Clinic> findById(String name) {
        return jdbc.sql("SELECT " + COLUMNS + " FROM VaxClinic WHERE Name = ?")
                .param(name).query(Rows::clinic).optional();
    }

    /** Every clinic with its shipped lots, staff counts and number of vaccinations given. */
    public List<ClinicOverview> findOverview() {
        Map<String, List<String>> lotsByClinic = new LinkedHashMap<>();
        jdbc.sql("SELECT Clinic, Lots FROM ShipTo ORDER BY Lots").query(rs -> {
            lotsByClinic.computeIfAbsent(rs.getString("Clinic"), k -> new ArrayList<>()).add(rs.getString("Lots"));
        });
        Map<String, Integer> nurses = countBy("SELECT VaxClinicName AS k, COUNT(*) AS c FROM NurseWork GROUP BY VaxClinicName");
        Map<String, Integer> doctors = countBy("SELECT VaxClinicName AS k, COUNT(*) AS c FROM DoctorWork GROUP BY VaxClinicName");
        Map<String, Integer> shots = countBy("SELECT ClinicName AS k, COUNT(*) AS c FROM Vaccination GROUP BY ClinicName");

        return findAll().stream()
                .map(c -> new ClinicOverview(c,
                        lotsByClinic.getOrDefault(c.name(), List.of()),
                        nurses.getOrDefault(c.name(), 0),
                        doctors.getOrDefault(c.name(), 0),
                        shots.getOrDefault(c.name(), 0)))
                .toList();
    }

    private Map<String, Integer> countBy(String sql) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        jdbc.sql(sql).query(rs -> {
            counts.put(rs.getString("k"), rs.getInt("c"));
        });
        return counts;
    }

    public void insert(Clinic c) {
        jdbc.sql("INSERT INTO VaxClinic (" + COLUMNS + ") VALUES (?, ?, ?, ?, ?, ?)")
                .params(c.name(), c.street(), c.city(), c.prov(), c.pc(), c.operatingDate())
                .update();
    }

    public boolean update(Clinic c) {
        return jdbc.sql("UPDATE VaxClinic SET Street = ?, City = ?, Prov = ?, PC = ?, date = ? WHERE Name = ?")
                .params(c.street(), c.city(), c.prov(), c.pc(), c.operatingDate(), c.name())
                .update() > 0;
    }

    public boolean delete(String name) {
        return jdbc.sql("DELETE FROM VaxClinic WHERE Name = ?").param(name).update() > 0;
    }
}
