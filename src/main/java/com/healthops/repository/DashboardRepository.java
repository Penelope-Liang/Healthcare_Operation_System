package com.healthops.repository;

import com.healthops.domain.DashboardSummary;
import com.healthops.domain.DashboardSummary.ManufacturerDoses;
import com.healthops.domain.Role;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DashboardRepository {

    private final JdbcClient jdbc;
    private final VaccinationRepository vaccinations;
    private final ClinicRepository clinics;
    private final StaffRepository staff;

    public DashboardRepository(JdbcClient jdbc, VaccinationRepository vaccinations,
                               ClinicRepository clinics, StaffRepository staff) {
        this.jdbc = jdbc;
        this.vaccinations = vaccinations;
        this.clinics = clinics;
        this.staff = staff;
    }

    public DashboardSummary summary() {
        List<ManufacturerDoses> byManufacturer = jdbc.sql("""
                SELECT manufacturer, COUNT(*) AS lots, COALESCE(SUM(doses), 0) AS doses
                FROM vaccine_lot GROUP BY manufacturer ORDER BY doses DESC, manufacturer
                """)
                .query((rs, n) -> new ManufacturerDoses(rs.getString("manufacturer"), rs.getInt("lots"), rs.getInt("doses")))
                .list();

        return new DashboardSummary(
                count("SELECT COUNT(*) FROM patient"),
                count("SELECT COUNT(DISTINCT patient_ohip) FROM vaccination"),
                count("SELECT COUNT(*) FROM vaccination"),
                count("SELECT COUNT(*) FROM clinic"),
                staff.count(Role.NURSE),
                staff.count(Role.DOCTOR),
                byManufacturer.stream().mapToInt(ManufacturerDoses::lots).sum(),
                byManufacturer.stream().mapToInt(ManufacturerDoses::doses).sum(),
                byManufacturer,
                vaccinations.findDetails(6),
                clinics.findOverview());
    }

    private int count(String sql) {
        return jdbc.sql(sql).query(Integer.class).single();
    }
}
