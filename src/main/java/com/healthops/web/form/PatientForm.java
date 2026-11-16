package com.healthops.web.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.healthops.domain.Patient;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public class PatientForm {

    @NotBlank(message = "OHIP is required.")
    @Pattern(regexp = Patterns.OHIP, message = "OHIP must use the format 0000-000-000.")
    private String ohip;

    @NotBlank(message = "First name is required.")
    @Pattern(regexp = Patterns.NAME, message = "First name " + Patterns.NAME_MESSAGE)
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Pattern(regexp = Patterns.NAME, message = "Last name " + Patterns.NAME_MESSAGE)
    private String lastName;

    @NotNull(message = "Date of birth is required.")
    @Past(message = "Date of birth must be in the past.")
    private LocalDate dateOfBirth;

    @Pattern(regexp = Patterns.OHIP, message = "Spouse OHIP must use the format 0000-000-000.")
    private String spouseOhip;

    @JsonIgnore
    @AssertTrue(message = "Spouse OHIP must be different from the patient's OHIP.")
    public boolean isSpouseDifferent() {
        return spouseOhip == null || !spouseOhip.equals(ohip);
    }

    public Patient toPatient() {
        return new Patient(ohip, firstName, lastName, dateOfBirth, spouseOhip);
    }

    public String getOhip() { return ohip; }
    public void setOhip(String ohip) { this.ohip = ohip; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getSpouseOhip() { return spouseOhip; }
    public void setSpouseOhip(String spouseOhip) { this.spouseOhip = spouseOhip; }
}
