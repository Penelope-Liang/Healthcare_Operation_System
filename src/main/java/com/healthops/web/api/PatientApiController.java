package com.healthops.web.api;

import com.healthops.domain.Patient;
import com.healthops.repository.PatientRepository;
import com.healthops.web.NotFoundException;
import com.healthops.web.form.PatientForm;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientApiController {

    private final PatientRepository patients;
    private final ApiSupport api;

    public PatientApiController(PatientRepository patients, ApiSupport api) {
        this.patients = patients;
        this.api = api;
    }

    @GetMapping
    public List<Patient> list() {
        return patients.findAll();
    }

    @GetMapping("/{ohip}")
    public Patient get(@PathVariable String ohip) {
        return patients.findById(ohip).orElseThrow(() -> notFound(ohip));
    }

    @PostMapping
    public ResponseEntity<Patient> create(@Valid @RequestBody PatientForm form) {
        Patient patient = form.toPatient();
        api.write(() -> patients.insert(patient),
                "A patient with OHIP " + patient.ohip() + " already exists.",
                "Unable to create patient.");
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{ohip}").buildAndExpand(patient.ohip()).toUri()).body(patient);
    }

    @PutMapping("/{ohip}")
    public Patient update(@PathVariable String ohip, @RequestBody PatientForm form) {
        form.setOhip(ohip);
        api.validate(form);
        Patient patient = form.toPatient();
        if (!patients.update(patient)) {
            throw notFound(ohip);
        }
        return patient;
    }

    @DeleteMapping("/{ohip}")
    public ResponseEntity<Void> delete(@PathVariable String ohip) {
        boolean deleted = api.write(() -> patients.delete(ohip), "",
                "Patient " + ohip + " still has vaccination records. Remove them first.");
        if (!deleted) {
            throw notFound(ohip);
        }
        return ResponseEntity.noContent().build();
    }

    private static NotFoundException notFound(String ohip) {
        return new NotFoundException("Patient " + ohip + " not found.");
    }
}
