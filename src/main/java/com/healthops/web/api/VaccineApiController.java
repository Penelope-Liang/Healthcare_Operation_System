package com.healthops.web.api;

import com.healthops.domain.Vaccine;
import com.healthops.repository.VaccineRepository;
import com.healthops.web.NotFoundException;
import com.healthops.web.form.VaccineForm;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/vaccines")
public class VaccineApiController {

    private final VaccineRepository vaccines;
    private final ApiSupport api;

    public VaccineApiController(VaccineRepository vaccines, ApiSupport api) {
        this.vaccines = vaccines;
        this.api = api;
    }

    @GetMapping
    public List<Vaccine> list() {
        return vaccines.findAll();
    }

    @GetMapping("/{lot}")
    public Vaccine get(@PathVariable String lot) {
        return vaccines.findById(lot).orElseThrow(() -> notFound(lot));
    }

    @PostMapping
    public ResponseEntity<Vaccine> create(@Valid @RequestBody VaccineForm form) {
        Vaccine vaccine = form.toVaccine();
        api.write(() -> vaccines.insert(vaccine),
                "Vaccine lot " + vaccine.lot() + " already exists.",
                "Manufacturer " + vaccine.companyName() + " does not exist.");
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{lot}").buildAndExpand(vaccine.lot()).toUri()).body(vaccine);
    }

    @PutMapping("/{lot}")
    public Vaccine update(@PathVariable String lot, @RequestBody VaccineForm form) {
        form.setLot(lot);
        api.validate(form);
        Vaccine vaccine = form.toVaccine();
        boolean updated = api.write(() -> vaccines.update(vaccine), "",
                "Manufacturer " + vaccine.companyName() + " does not exist.");
        if (!updated) {
            throw notFound(lot);
        }
        return vaccine;
    }

    @DeleteMapping("/{lot}")
    public ResponseEntity<Void> delete(@PathVariable String lot) {
        boolean deleted = api.write(() -> vaccines.delete(lot), "",
                "Lot " + lot + " is used by vaccination records. Remove them first.");
        if (!deleted) {
            throw notFound(lot);
        }
        return ResponseEntity.noContent().build();
    }

    private static NotFoundException notFound(String lot) {
        return new NotFoundException("Vaccine lot " + lot + " not found.");
    }
}
