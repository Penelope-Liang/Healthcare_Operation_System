package com.healthops.web.form;

import com.healthops.domain.Patient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class PatientForm {

    @NotBlank(message = "OHIP is required.")
    @Pattern(regexp = Patterns.OHIP, message = "OHIP must use the format 0000-000-000.")
    private String ohip;

    @Pattern(regexp = Patterns.OHIP, message = "Spouse OHIP must use the format 0000-000-000.")
    private String spouseOhip;

    @NotBlank(message = "First name is required.")
    @Pattern(regexp = "[A-Za-z]{1,30}", message = "First name must be 1-30 letters.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Pattern(regexp = "[A-Za-z]{1,30}", message = "Last name must be 1-30 letters.")
    private String lastName;

    @NotNull(message = "Date of birth is required.")
    @Past(message = "Date of birth must be in the past.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dob;

    public Patient toPatient() {
        return new Patient(ohip, spouseOhip, firstName, lastName, dob.toString());
    }

    public String getOhip() { return ohip; }
    public void setOhip(String ohip) { this.ohip = ohip; }
    public String getSpouseOhip() { return spouseOhip; }
    public void setSpouseOhip(String spouseOhip) { this.spouseOhip = spouseOhip; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
}
