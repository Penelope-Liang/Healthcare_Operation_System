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
    private final WorkerRepository workers;

    public DashboardRepository(JdbcClient jdbc, VaccinationRepository vaccinations,
                               ClinicRepository clinics, WorkerRepository workers) {
        this.jdbc = jdbc;
        this.vaccinations = vaccinations;
        this.clinics = clinics;
        this.workers = workers;
    }

    public DashboardSummary summary() {
        List<ManufacturerDoses> byManufacturer = jdbc.sql("""
                SELECT CompanyName, COUNT(*) AS lots, COALESCE(SUM(Doses), 0) AS doses
                FROM Vaccine GROUP BY CompanyName ORDER BY doses DESC, CompanyName
                """)
                .query((rs, n) -> new ManufacturerDoses(rs.getString("CompanyName"), rs.getInt("lots"), rs.getInt("doses")))
                .list();

        return new DashboardSummary(
                count("Patient"),
                count("Vaccination"),
                count("VaxClinic"),
                workers.count(Role.NURSE),
                workers.count(Role.DOCTOR),
                byManufacturer.stream().mapToInt(ManufacturerDoses::lots).sum(),
                byManufacturer.stream().mapToInt(ManufacturerDoses::doses).sum(),
                byManufacturer,
                vaccinations.findDetails(6),
                clinics.findOverview());
    }

    private int count(String table) {
        return jdbc.sql("SELECT COUNT(*) FROM " + table).query(Integer.class).single();
    }
}
