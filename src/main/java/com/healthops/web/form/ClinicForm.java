package com.healthops.web.form;

import com.healthops.domain.Clinic;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class ClinicForm {

    @NotBlank(message = "Clinic name is required.")
    @Size(max = 50, message = "Clinic name must be at most 50 characters.")
    private String name;

    @NotBlank(message = "Street is required.")
    @Size(max = 60, message = "Street must be at most 60 characters.")
    private String street;

    @NotBlank(message = "City is required.")
    @Size(max = 40, message = "City must be at most 40 characters.")
    private String city;

    @NotBlank(message = "Province is required.")
    @Size(max = 30, message = "Province must be at most 30 characters.")
    private String province;

    @NotBlank(message = "Postal code is required.")
    @Size(max = 10, message = "Postal code must be at most 10 characters.")
    private String postalCode;

    @NotNull(message = "Operating date is required.")
    private LocalDate operatingDate;

    public Clinic toClinic() {
        return new Clinic(name, street, city, province, postalCode, operatingDate);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public LocalDate getOperatingDate() { return operatingDate; }
    public void setOperatingDate(LocalDate operatingDate) { this.operatingDate = operatingDate; }
}
