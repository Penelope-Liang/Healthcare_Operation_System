package com.healthops.domain;

import java.util.List;

public record DashboardSummary(
        int patients,
        int vaccinated,
        int clinics,
        int nurses,
        int doctors,
        int vaccineLots,
        int dosesInInventory,
        List<ManufacturerDoses> dosesByManufacturer,
        List<VaccinationDetail> recentVaccinations,
        List<ClinicOverview> clinicCoverage) {

    public int staff() {
        return nurses + doctors;
    }

    /** Share of registered patients with a vaccination record, 0-100. */
    public int coveragePercent() {
        return patients == 0 ? 0 : Math.round(vaccinated * 100f / patients);
    }

    public record ManufacturerDoses(String manufacturer, int lots, int doses) {
    }
}
