package com.healthops.repository;

import com.healthops.domain.Role;
import com.healthops.domain.StaffMember;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Nurses and doctors live in one staff table; credentials and clinic assignments are child tables. */
@Repository
public class StaffRepository {

    private final JdbcClient jdbc;

    public StaffRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<StaffMember> findAll() {
        return load(null, null);
    }

    public List<StaffMember> findByRole(Role role) {
        return load("s.role = ?", role.key());
    }

    public Optional<StaffMember> findById(String id) {
        return load("s.id = ?", id).stream().findFirst();
    }

    public int count(Role role) {
        return jdbc.sql("SELECT COUNT(*) FROM staff WHERE role = ?").param(role.key()).query(Integer.class).single();
    }

    @Transactional
    public void insert(StaffMember m) {
        jdbc.sql("INSERT INTO staff (id, role, first_name, last_name) VALUES (?, ?, ?, ?)")
                .params(m.id(), m.role().key(), m.firstName(), m.lastName())
                .update();
        saveChildren(m);
    }

    @Transactional
    public boolean update(StaffMember m) {
        int updated = jdbc.sql("UPDATE staff SET role = ?, first_name = ?, last_name = ? WHERE id = ?")
                .params(m.role().key(), m.firstName(), m.lastName(), m.id())
                .update();
        if (updated == 0) {
            return false;
        }
        jdbc.sql("DELETE FROM staff_credential WHERE staff_id = ?").param(m.id()).update();
        jdbc.sql("DELETE FROM staff_assignment WHERE staff_id = ?").param(m.id()).update();
        saveChildren(m);
        return true;
    }

    /** Credentials and assignments are removed by ON DELETE CASCADE. */
    public boolean delete(String id) {
        return jdbc.sql("DELETE FROM staff WHERE id = ?").param(id).update() > 0;
    }

    private void saveChildren(StaffMember m) {
        for (String credential : m.credentials()) {
            jdbc.sql("INSERT INTO staff_credential (staff_id, credential) VALUES (?, ?)").params(m.id(), credential).update();
        }
        for (String clinic : m.clinics()) {
            jdbc.sql("INSERT INTO staff_assignment (staff_id, clinic_name) VALUES (?, ?)").params(m.id(), clinic).update();
        }
    }

    private List<StaffMember> load(String condition, Object param) {
        String where = condition == null ? "" : " WHERE " + condition;
        Map<String, List<String>> credentials = new HashMap<>();
        Map<String, List<String>> clinics = new HashMap<>();

        var credentialQuery = jdbc.sql("SELECT c.staff_id, c.credential FROM staff_credential c JOIN staff s ON s.id = c.staff_id"
                + where + " ORDER BY c.credential");
        (param == null ? credentialQuery : credentialQuery.param(param)).query(rs -> {
            credentials.computeIfAbsent(rs.getString("staff_id"), k -> new ArrayList<>()).add(rs.getString("credential"));
        });
        var clinicQuery = jdbc.sql("SELECT a.staff_id, a.clinic_name FROM staff_assignment a JOIN staff s ON s.id = a.staff_id"
                + where + " ORDER BY a.clinic_name");
        (param == null ? clinicQuery : clinicQuery.param(param)).query(rs -> {
            clinics.computeIfAbsent(rs.getString("staff_id"), k -> new ArrayList<>()).add(rs.getString("clinic_name"));
        });

        var staffQuery = jdbc.sql("SELECT s.id, s.role, s.first_name, s.last_name FROM staff s" + where
                + " ORDER BY s.last_name, s.first_name");
        return (param == null ? staffQuery : staffQuery.param(param))
                .query((rs, n) -> {
                    String id = rs.getString("id");
                    return new StaffMember(id, Role.from(rs.getString("role")), rs.getString("first_name"),
                            rs.getString("last_name"), credentials.getOrDefault(id, List.of()),
                            clinics.getOrDefault(id, List.of()));
                })
                .list();
    }
}
