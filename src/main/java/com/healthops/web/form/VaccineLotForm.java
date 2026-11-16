package com.healthops.web.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.healthops.domain.VaccineLot;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public class VaccineLotForm {

    @NotBlank(message = "Lot is required.")
    @Pattern(regexp = Patterns.LOT, message = "Lot must be 1-12 letters or numbers.")
    private String lot;

    @NotBlank(message = "Manufacturer is required.")
    private String manufacturer;

    @NotNull(message = "Production date is required.")
    private LocalDate productionDate;

    @NotNull(message = "Expiry date is required.")
    private LocalDate expiryDate;

    @NotNull(message = "Doses is required.")
    @Min(value = 0, message = "Doses must be at least 0.")
    private Integer doses;

    @JsonIgnore
    @AssertTrue(message = "Expiry date must be after the production date.")
    public boolean isExpiryAfterProduction() {
        return productionDate == null || expiryDate == null || expiryDate.isAfter(productionDate);
    }

    public VaccineLot toVaccineLot() {
        return new VaccineLot(lot, manufacturer, productionDate, expiryDate, doses);
    }

    public String getLot() { return lot; }
    public void setLot(String lot) { this.lot = lot; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public LocalDate getProductionDate() { return productionDate; }
    public void setProductionDate(LocalDate productionDate) { this.productionDate = productionDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public Integer getDoses() { return doses; }
    public void setDoses(Integer doses) { this.doses = doses; }
}
