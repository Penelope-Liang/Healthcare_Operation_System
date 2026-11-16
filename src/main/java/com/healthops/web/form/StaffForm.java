package com.healthops.web.form;

import com.healthops.domain.Role;
import com.healthops.domain.StaffMember;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

public class StaffForm {

    @NotBlank(message = "Staff ID is required.")
    @Pattern(regexp = Patterns.STAFF_ID, message = "Staff ID must be 1-5 letters or numbers.")
    private String id;

    @NotBlank(message = "Role is required.")
    @Pattern(regexp = "(?i)nurse|doctor", message = "Role must be nurse or doctor.")
    private String role;

    @NotBlank(message = "First name is required.")
    @Pattern(regexp = Patterns.NAME, message = "First name " + Patterns.NAME_MESSAGE)
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Pattern(regexp = Patterns.NAME, message = "Last name " + Patterns.NAME_MESSAGE)
    private String lastName;

    @NotEmpty(message = "At least one credential is required.")
    private List<@NotBlank(message = "Credentials cannot be blank.")
                 @Size(max = 20, message = "Each credential must be at most 20 characters.") String> credentials = new ArrayList<>();

    /** Clinic assignments; may be empty for staff who are not assigned yet. */
    private List<@NotBlank(message = "Clinic names cannot be blank.") String> clinics = new ArrayList<>();

    public StaffMember toStaffMember() {
        return new StaffMember(id, Role.from(role), firstName, lastName, distinct(credentials), distinct(clinics));
    }

    private static List<String> distinct(List<String> values) {
        return List.copyOf(new LinkedHashSet<>(values.stream().filter(Objects::nonNull).toList()));
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public List<String> getCredentials() { return credentials; }
    public void setCredentials(List<String> credentials) { this.credentials = credentials == null ? new ArrayList<>() : credentials; }
    public List<String> getClinics() { return clinics; }
    public void setClinics(List<String> clinics) { this.clinics = clinics == null ? new ArrayList<>() : clinics; }
}
