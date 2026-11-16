package com.healthops.repository;

import com.healthops.domain.Shipment;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class ShipmentRepository {

    private static final String SELECT = """
            SELECT s.lot, s.clinic_name, l.manufacturer, l.doses
            FROM shipment s JOIN vaccine_lot l ON l.lot = s.lot
            """;

    private final JdbcClient jdbc;

    public ShipmentRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<Shipment> findAll() {
        return jdbc.sql(SELECT + " ORDER BY s.clinic_name, s.lot").query(ShipmentRepository::map).list();
    }

    public Optional<Shipment> find(String lot, String clinicName) {
        return jdbc.sql(SELECT + " WHERE s.lot = ? AND s.clinic_name = ?")
                .params(lot, clinicName).query(ShipmentRepository::map).optional();
    }

    public void insert(String lot, String clinicName) {
        jdbc.sql("INSERT INTO shipment (lot, clinic_name) VALUES (?, ?)").params(lot, clinicName).update();
    }

    public boolean delete(String lot, String clinicName) {
        return jdbc.sql("DELETE FROM shipment WHERE lot = ? AND clinic_name = ?").params(lot, clinicName).update() > 0;
    }

    private static Shipment map(ResultSet rs, int rowNum) throws SQLException {
        return new Shipment(rs.getString("lot"), rs.getString("clinic_name"), rs.getString("manufacturer"), rs.getInt("doses"));
    }
}
