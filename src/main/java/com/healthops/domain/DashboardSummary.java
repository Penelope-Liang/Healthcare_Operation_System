package com.healthops.domain;

import java.util.List;

public record DashboardSummary(
        int patients,
        int vaccinatedPatients,
        int dosesAdministered,
        int clinics,
        int nurses,
        int doctors,
        int vaccineLots,
        int dosesInInventory,
        List<ManufacturerDoses> dosesByManufacturer,
        List<VaccinationDetail> recentVaccinations,
        List<ClinicOverview> clinicCoverage) {

    public record ManufacturerDoses(String manufacturer, int lots, int doses) {
    }
}
