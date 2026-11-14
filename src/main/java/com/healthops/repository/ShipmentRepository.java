package com.healthops.repository;

import com.healthops.domain.Shipment;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ShipmentRepository {

    private static final String SELECT = """
            SELECT ShipTo.Lots, ShipTo.Clinic, Vaccine.CompanyName, Vaccine.Doses
            FROM ShipTo JOIN Vaccine ON Vaccine.Lot = ShipTo.Lots
            """;

    private final JdbcClient jdbc;

    public ShipmentRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<Shipment> findAll() {
        return jdbc.sql(SELECT + " ORDER BY ShipTo.Clinic, ShipTo.Lots").query(ShipmentRepository::map).list();
    }

    public Optional<Shipment> find(String lot, String clinic) {
        return jdbc.sql(SELECT + " WHERE ShipTo.Lots = ? AND ShipTo.Clinic = ?")
                .params(lot, clinic).query(ShipmentRepository::map).optional();
    }

    public void insert(String lot, String clinic) {
        jdbc.sql("INSERT INTO ShipTo (Lots, Clinic) VALUES (?, ?)").params(lot, clinic).update();
    }

    public boolean delete(String lot, String clinic) {
        return jdbc.sql("DELETE FROM ShipTo WHERE Lots = ? AND Clinic = ?").params(lot, clinic).update() > 0;
    }

    private static Shipment map(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return new Shipment(rs.getString("Lots"), rs.getString("Clinic"), rs.getString("CompanyName"), rs.getInt("Doses"));
    }
}
