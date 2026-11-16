package com.healthops.repository;

import com.healthops.domain.Company;
import com.healthops.domain.VaccineLot;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static com.healthops.repository.Columns.date;

@Repository
public class VaccineLotRepository {

    private static final String SELECT =
            "SELECT lot, manufacturer, production_date, expiry_date, doses FROM vaccine_lot";

    private final JdbcClient jdbc;

    public VaccineLotRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<VaccineLot> findAll() {
        return jdbc.sql(SELECT + " ORDER BY manufacturer, lot").query(VaccineLotRepository::map).list();
    }

    public Optional<VaccineLot> findById(String lot) {
        return jdbc.sql(SELECT + " WHERE lot = ?").param(lot).query(VaccineLotRepository::map).optional();
    }

    public List<Company> findCompanies() {
        return jdbc.sql("SELECT name, street, city, province, postal_code FROM company ORDER BY name")
                .query((rs, n) -> new Company(rs.getString("name"), rs.getString("street"), rs.getString("city"),
                        rs.getString("province"), rs.getString("postal_code")))
                .list();
    }

    public void insert(VaccineLot v) {
        jdbc.sql("INSERT INTO vaccine_lot (lot, manufacturer, production_date, expiry_date, doses) VALUES (?, ?, ?, ?, ?)")
                .params(v.lot(), v.manufacturer(), v.productionDate(), v.expiryDate(), v.doses())
                .update();
    }

    public boolean update(VaccineLot v) {
        return jdbc.sql("UPDATE vaccine_lot SET manufacturer = ?, production_date = ?, expiry_date = ?, doses = ? WHERE lot = ?")
                .params(v.manufacturer(), v.productionDate(), v.expiryDate(), v.doses(), v.lot())
                .update() > 0;
    }

    /** Shipments cascade; vaccinations from this lot block the delete. */
    public boolean delete(String lot) {
        return jdbc.sql("DELETE FROM vaccine_lot WHERE lot = ?").param(lot).update() > 0;
    }

    private static VaccineLot map(ResultSet rs, int rowNum) throws SQLException {
        return new VaccineLot(rs.getString("lot"), rs.getString("manufacturer"), date(rs, "production_date"),
                date(rs, "expiry_date"), rs.getInt("doses"));
    }
}
