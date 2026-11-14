package com.healthops.web.form;

import com.healthops.domain.Role;
import com.healthops.domain.Worker;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class WorkerForm {

    @NotBlank(message = "Role is required.")
    @Pattern(regexp = "(?i)nurse|doctor", message = "Role must be nurse or doctor.")
    private String role;

    @NotBlank(message = "Worker ID is required.")
    @Pattern(regexp = Patterns.WORKER_ID, message = "Worker ID must be 1-5 letters or numbers.")
    private String id;

    @NotBlank(message = "First name is required.")
    @Pattern(regexp = Patterns.WORKER_NAME, message = "First name must be 1-15 letters.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Pattern(regexp = Patterns.WORKER_NAME, message = "Last name must be 1-15 letters.")
    private String lastName;

    @NotBlank(message = "Credential is required.")
    @Size(max = 20, message = "Credential must be at most 20 characters.")
    private String credential;

    @NotBlank(message = "Clinic is required.")
    private String clinicName;

    public Worker toWorker() {
        return new Worker(Role.from(role), id, firstName, lastName, credential, clinicName);
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getCredential() { return credential; }
    public void setCredential(String credential) { this.credential = credential; }
    public String getClinicName() { return clinicName; }
    public void setClinicName(String clinicName) { this.clinicName = clinicName; }
}
