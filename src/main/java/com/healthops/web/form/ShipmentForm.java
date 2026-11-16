package com.healthops.web.form;

import jakarta.validation.constraints.NotBlank;

public class ShipmentForm {

    @NotBlank(message = "Lot is required.")
    private String lot;

    @NotBlank(message = "Clinic is required.")
    private String clinicName;

    public String getLot() { return lot; }
    public void setLot(String lot) { this.lot = lot; }
    public String getClinicName() { return clinicName; }
    public void setClinicName(String clinicName) { this.clinicName = clinicName; }
}
