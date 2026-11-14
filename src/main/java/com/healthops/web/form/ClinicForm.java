package com.healthops.web.form;

import com.healthops.domain.Clinic;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class ClinicForm {

    @NotBlank(message = "Clinic name is required.")
    @Size(max = 30, message = "Clinic name must be at most 30 characters.")
    private String name;

    @NotBlank(message = "Street is required.")
    @Size(max = 30, message = "Street must be at most 30 characters.")
    private String street;

    @NotBlank(message = "City is required.")
    @Size(max = 30, message = "City must be at most 30 characters.")
    private String city;

    @NotBlank(message = "Province is required.")
    @Size(max = 30, message = "Province must be at most 30 characters.")
    private String prov;

    @NotBlank(message = "Postal code is required.")
    @Size(max = 30, message = "Postal code must be at most 30 characters.")
    private String pc;

    @NotNull(message = "Operating date is required.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate operatingDate;

    public Clinic toClinic() {
        return new Clinic(name, street, city, prov, pc, operatingDate.toString());
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getProv() { return prov; }
    public void setProv(String prov) { this.prov = prov; }
    public String getPc() { return pc; }
    public void setPc(String pc) { this.pc = pc; }
    public LocalDate getOperatingDate() { return operatingDate; }
    public void setOperatingDate(LocalDate operatingDate) { this.operatingDate = operatingDate; }
}
