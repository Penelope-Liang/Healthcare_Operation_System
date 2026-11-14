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

    @GetMapping("/{ohip}")
    public Vaccination get(@PathVariable String ohip) {
        return vaccinations.findById(ohip).orElseThrow(() -> notFound(ohip));
    }

    @PostMapping
    public ResponseEntity<Vaccination> create(@Valid @RequestBody VaccinationForm form) {
        Vaccination vaccination = form.toVaccination();
        api.write(() -> vaccinations.insert(vaccination),
                "Patient " + vaccination.ohip() + " already has a vaccination record.",
                UNKNOWN_REFERENCE);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{ohip}").buildAndExpand(vaccination.ohip()).toUri()).body(vaccination);
    }

    @PutMapping("/{ohip}")
    public Vaccination update(@PathVariable String ohip, @RequestBody VaccinationForm form) {
        form.setOhip(ohip);
        api.validate(form);
        Vaccination vaccination = form.toVaccination();
        boolean updated = api.write(() -> vaccinations.update(vaccination), "", UNKNOWN_REFERENCE);
        if (!updated) {
            throw notFound(ohip);
        }
        return vaccination;
    }

    @DeleteMapping("/{ohip}")
    public ResponseEntity<Void> delete(@PathVariable String ohip) {
        if (!vaccinations.delete(ohip)) {
            throw notFound(ohip);
        }
        return ResponseEntity.noContent().build();
    }

    private static NotFoundException notFound(String ohip) {
        return new NotFoundException("No vaccination record for patient " + ohip + ".");
    }
}
