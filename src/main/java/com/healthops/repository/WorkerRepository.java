package com.healthops.repository;

import com.healthops.domain.Role;
import com.healthops.domain.Worker;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Nurses and doctors are each spread over three tables (person, credential, clinic assignment).
 * Table names come from the {@link Role} enum, never from user input.
 */
@Repository
public class WorkerRepository {

    private final JdbcClient jdbc;

    public WorkerRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    private static String select(Role r) {
        return """
                SELECT w.Id, w.FirstName, w.LastName, c.%2$s AS Credential, k.VaxClinicName
                FROM %1$s w
                LEFT JOIN %3$s c ON c.%4$s = w.Id
                LEFT JOIN %5$s k ON k.%4$s = w.Id
                """.formatted(r.workerTable, r.credentialColumn, r.credsTable, r.idColumn, r.workTable);
    }

    public List<Worker> findAll() {
        List<Worker> all = new ArrayList<>(findByRole(Role.NURSE));
        all.addAll(findByRole(Role.DOCTOR));
        all.sort(Comparator.comparing(Worker::lastName).thenComparing(Worker::firstName));
        return all;
    }

    public List<Worker> findByRole(Role role) {
        return jdbc.sql(select(role) + " ORDER BY w.LastName, w.FirstName")
                .query((rs, n) -> map(role, rs)).list();
    }

    /** A worker can be assigned to several clinics; the first assignment is returned. */
    public Optional<Worker> findById(Role role, String id) {
        return jdbc.sql(select(role) + " WHERE w.Id = ? ORDER BY k.VaxClinicName")
                .param(id).query((rs, n) -> map(role, rs)).list().stream().findFirst();
    }

    public int count(Role role) {
        return jdbc.sql("SELECT COUNT(*) FROM " + role.workerTable).query(Integer.class).single();
    }

    @Transactional
    public void insert(Worker w) {
        Role r = w.role();
        jdbc.sql("INSERT INTO " + r.workerTable + " (Id, FirstName, LastName) VALUES (?, ?, ?)")
                .params(w.id(), w.firstName(), w.lastName()).update();
        jdbc.sql("INSERT INTO " + r.credsTable + " (" + r.idColumn + ", " + r.credentialColumn + ") VALUES (?, ?)")
                .params(w.id(), w.credential()).update();
        jdbc.sql("INSERT INTO " + r.workTable + " (VaxClinicName, " + r.idColumn + ") VALUES (?, ?)")
                .params(w.clinicName(), w.id()).update();
    }

    @Transactional
    public boolean update(Worker w) {
        Role r = w.role();
        int updated = jdbc.sql("UPDATE " + r.workerTable + " SET FirstName = ?, LastName = ? WHERE Id = ?")
                .params(w.firstName(), w.lastName(), w.id()).update();
        if (updated == 0) {
            return false;
        }
        jdbc.sql("DELETE FROM " + r.credsTable + " WHERE " + r.idColumn + " = ?").param(w.id()).update();
        jdbc.sql("INSERT INTO " + r.credsTable + " (" + r.idColumn + ", " + r.credentialColumn + ") VALUES (?, ?)")
                .params(w.id(), w.credential()).update();
        jdbc.sql("DELETE FROM " + r.workTable + " WHERE " + r.idColumn + " = ?").param(w.id()).update();
        jdbc.sql("INSERT INTO " + r.workTable + " (VaxClinicName, " + r.idColumn + ") VALUES (?, ?)")
                .params(w.clinicName(), w.id()).update();
        return true;
    }

    @Transactional
    public boolean delete(Role r, String id) {
        jdbc.sql("DELETE FROM " + r.workTable + " WHERE " + r.idColumn + " = ?").param(id).update();
        jdbc.sql("DELETE FROM " + r.credsTable + " WHERE " + r.idColumn + " = ?").param(id).update();
        return jdbc.sql("DELETE FROM " + r.workerTable + " WHERE Id = ?").param(id).update() > 0;
    }

    private static Worker map(Role role, java.sql.ResultSet rs) throws java.sql.SQLException {
        return new Worker(role, rs.getString("Id"), rs.getString("FirstName"), rs.getString("LastName"),
                rs.getString("Credential"), rs.getString("VaxClinicName"));
    }
}
