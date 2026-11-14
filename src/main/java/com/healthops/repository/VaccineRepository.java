package com.healthops.repository;

import com.healthops.domain.Company;
import com.healthops.domain.Vaccine;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class VaccineRepository {

    private static final String COLUMNS = "Lot, CompanyName, Prodcution, Expiry, Doses";

    private final JdbcClient jdbc;

    public VaccineRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<Vaccine> findAll() {
        return jdbc.sql("SELECT " + COLUMNS + " FROM Vaccine ORDER BY CompanyName, Lot").query(Rows::vaccine).list();
    }

    public Optional<Vaccine> findById(String lot) {
        return jdbc.sql("SELECT " + COLUMNS + " FROM Vaccine WHERE Lot = ?").param(lot).query(Rows::vaccine).optional();
    }

    public List<Company> findCompanies() {
        return jdbc.sql("SELECT Name, Street, City, Prov, PC FROM Company ORDER BY Name")
                .query((rs, n) -> new Company(rs.getString("Name"), rs.getString("Street"), rs.getString("City"),
                        rs.getString("Prov"), rs.getString("PC")))
                .list();
    }

    public void insert(Vaccine v) {
        jdbc.sql("INSERT INTO Vaccine (" + COLUMNS + ") VALUES (?, ?, ?, ?, ?)")
                .params(v.lot(), v.companyName(), v.production(), v.expiry(), v.doses())
                .update();
    }

    public boolean update(Vaccine v) {
        return jdbc.sql("UPDATE Vaccine SET CompanyName = ?, Prodcution = ?, Expiry = ?, Doses = ? WHERE Lot = ?")
                .params(v.companyName(), v.production(), v.expiry(), v.doses(), v.lot())
                .update() > 0;
    }

    public boolean delete(String lot) {
        return jdbc.sql("DELETE FROM Vaccine WHERE Lot = ?").param(lot).update() > 0;
    }
}
