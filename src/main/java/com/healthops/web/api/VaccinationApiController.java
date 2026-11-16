package com.healthops.web.api;

import com.healthops.domain.Vaccination;
import com.healthops.repository.VaccinationRepository;
import com.healthops.web.NotFoundException;
import com.healthops.web.form.VaccinationForm;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

/** One record per dose; a patient can have several (first dose, second dose, boosters). */
@RestController
@RequestMapping("/api/vaccinations")
public class VaccinationApiController {

    private static final String UNKNOWN_REFERENCE = "Unknown patient, clinic or vaccine lot.";

    private final VaccinationRepository vaccinations;
    private final ApiSupport api;

    public VaccinationApiController(VaccinationRepository vaccinations, ApiSupport api) {
        this.vaccinations = vaccinations;
        this.api = api;
    }

    @GetMapping
    public List<Vaccination> list() {
        return vaccinations.findAll();
    }

    @GetMapping("/{id}")
    public Vaccination get(@PathVariable long id) {
        return vaccinations.findById(id).orElseThrow(() -> notFound(id));
    }

    @PostMapping
    public ResponseEntity<Vaccination> create(@Valid @RequestBody VaccinationForm form) {
        Vaccination saved = api.write(() -> vaccinations.insert(form.toVaccination(null)), "", UNKNOWN_REFERENCE);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(saved.id()).toUri()).body(saved);
    }

    @PutMapping("/{id}")
    public Vaccination update(@PathVariable long id, @Valid @RequestBody VaccinationForm form) {
        Vaccination vaccination = form.toVaccination(id);
        boolean found = api.write(() -> vaccinations.update(vaccination), "", UNKNOWN_REFERENCE);
        if (!found) {
            throw notFound(id);
        }
        return vaccination;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        if (!vaccinations.delete(id)) {
            throw notFound(id);
        }
        return ResponseEntity.noContent().build();
    }

    private static NotFoundException notFound(long id) {
        return new NotFoundException("Vaccination record " + id + " not found.");
    }
}
