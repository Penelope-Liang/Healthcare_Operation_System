package com.healthops.web.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.healthops.domain.Vaccine;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class VaccineForm {

    @NotBlank(message = "Lot is required.")
    @Pattern(regexp = Patterns.LOT, message = "Lot must be 1-6 letters or numbers.")
    private String lot;

    @NotBlank(message = "Manufacturer is required.")
    private String companyName;

    @NotNull(message = "Production date is required.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate production;

    @NotNull(message = "Expiry date is required.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate expiry;

    @NotNull(message = "Doses is required.")
    @Min(value = 0, message = "Doses must be at least 0.")
    private Integer doses;

    @JsonIgnore
    @AssertTrue(message = "Expiry date must be after the production date.")
    public boolean isExpiryAfterProduction() {
        return production == null || expiry == null || expiry.isAfter(production);
    }

    public Vaccine toVaccine() {
        return new Vaccine(lot, companyName, production.toString(), expiry.toString(), doses);
    }

    public String getLot() { return lot; }
    public void setLot(String lot) { this.lot = lot; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public LocalDate getProduction() { return production; }
    public void setProduction(LocalDate production) { this.production = production; }
    public LocalDate getExpiry() { return expiry; }
    public void setExpiry(LocalDate expiry) { this.expiry = expiry; }
    public Integer getDoses() { return doses; }
    public void setDoses(Integer doses) { this.doses = doses; }
}
