package com.healthops.web.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.healthops.domain.Vaccination;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class VaccinationForm {

    @NotBlank(message = "Patient is required.")
    private String ohip;

    @NotBlank(message = "Clinic is required.")
    private String clinicName;

    @NotBlank(message = "Vaccine lot is required.")
    private String lot;

    @NotNull(message = "Date is required.")
    private LocalDate date;

    @NotNull(message = "Time is required.")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime time;

    public Vaccination toVaccination(Long id) {
        return new Vaccination(id, ohip, clinicName, lot, date, time);
    }

    public String getOhip() { return ohip; }
    public void setOhip(String ohip) { this.ohip = ohip; }
    public String getClinicName() { return clinicName; }
    public void setClinicName(String clinicName) { this.clinicName = clinicName; }
    public String getLot() { return lot; }
    public void setLot(String lot) { this.lot = lot; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }
}
