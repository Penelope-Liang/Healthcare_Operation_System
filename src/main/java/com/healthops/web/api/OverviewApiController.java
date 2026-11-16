package com.healthops.web.api;

import com.healthops.domain.ClinicOverview;
import com.healthops.domain.Company;
import com.healthops.domain.DashboardSummary;
import com.healthops.domain.PatientOverview;
import com.healthops.domain.VaccinationDetail;
import com.healthops.repository.ClinicRepository;
import com.healthops.repository.DashboardRepository;
import com.healthops.repository.PatientRepository;
import com.healthops.repository.VaccinationRepository;
import com.healthops.repository.VaccineRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Read-only, pre-joined views that the Vue pages render directly. */
@RestController
@RequestMapping("/api")
public class OverviewApiController {

    private final DashboardRepository dashboard;
    private final PatientRepository patients;
    private final VaccinationRepository vaccinations;
    private final ClinicRepository clinics;
    private final VaccineRepository vaccines;

    public OverviewApiController(DashboardRepository dashboard, PatientRepository patients,
                                 VaccinationRepository vaccinations, ClinicRepository clinics,
                                 VaccineRepository vaccines) {
        this.dashboard = dashboard;
        this.patients = patients;
        this.vaccinations = vaccinations;
        this.clinics = clinics;
        this.vaccines = vaccines;
    }

    @GetMapping("/dashboard")
    public DashboardSummary dashboard() {
        return dashboard.summary();
    }

    /** Every patient with their vaccination (if any), vaccine manufacturer and clinic. */
    @GetMapping("/patients/overview")
    public List<PatientOverview> patientOverview() {
        return patients.findOverview();
    }

    /** Vaccination records with patient and manufacturer names, newest first. */
    @GetMapping("/vaccinations/details")
    public List<VaccinationDetail> vaccinationDetails() {
        return vaccinations.findDetails(Integer.MAX_VALUE);
    }

    /** Clinics with shipped lots, staff counts and doses given. */
    @GetMapping("/clinics/overview")
    public List<ClinicOverview> clinicOverview() {
        return clinics.findOverview();
    }

    @GetMapping("/companies")
    public List<Company> companies() {
        return vaccines.findCompanies();
    }
}
