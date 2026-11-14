package com.healthops.web.api;

import com.healthops.domain.Clinic;
import com.healthops.repository.ClinicRepository;
import com.healthops.web.NotFoundException;
import com.healthops.web.form.ClinicForm;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/clinics")
public class ClinicApiController {

    private final ClinicRepository clinics;
    private final ApiSupport api;

    public ClinicApiController(ClinicRepository clinics, ApiSupport api) {
        this.clinics = clinics;
        this.api = api;
    }

    @GetMapping
    public List<Clinic> list() {
        return clinics.findAll();
    }

    @GetMapping("/{name}")
    public Clinic get(@PathVariable String name) {
        return clinics.findById(name).orElseThrow(() -> notFound(name));
    }

    @PostMapping
    public ResponseEntity<Clinic> create(@Valid @RequestBody ClinicForm form) {
        Clinic clinic = form.toClinic();
        api.write(() -> clinics.insert(clinic),
                "A clinic named " + clinic.name() + " already exists.",
                "Unable to create clinic.");
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{name}").buildAndExpand(clinic.name()).toUri()).body(clinic);
    }

    @PutMapping("/{name}")
    public Clinic update(@PathVariable String name, @RequestBody ClinicForm form) {
        form.setName(name);
        api.validate(form);
        Clinic clinic = form.toClinic();
        if (!clinics.update(clinic)) {
            throw notFound(name);
        }
        return clinic;
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> delete(@PathVariable String name) {
        boolean deleted = api.write(() -> clinics.delete(name), "",
                "Clinic " + name + " still has vaccination records. Remove them first.");
        if (!deleted) {
            throw notFound(name);
        }
        return ResponseEntity.noContent().build();
    }

    private static NotFoundException notFound(String name) {
        return new NotFoundException("Clinic " + name + " not found.");
    }
}
