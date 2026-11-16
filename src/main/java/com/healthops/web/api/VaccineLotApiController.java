package com.healthops.web.api;

import com.healthops.domain.VaccineLot;
import com.healthops.repository.VaccineLotRepository;
import com.healthops.web.NotFoundException;
import com.healthops.web.form.VaccineLotForm;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/vaccines")
public class VaccineLotApiController {

    private final VaccineLotRepository lots;
    private final ApiSupport api;

    public VaccineLotApiController(VaccineLotRepository lots, ApiSupport api) {
        this.lots = lots;
        this.api = api;
    }

    @GetMapping
    public List<VaccineLot> list() {
        return lots.findAll();
    }

    @GetMapping("/{lot}")
    public VaccineLot get(@PathVariable String lot) {
        return lots.findById(lot).orElseThrow(() -> notFound(lot));
    }

    @PostMapping
    public ResponseEntity<VaccineLot> create(@Valid @RequestBody VaccineLotForm form) {
        VaccineLot lot = form.toVaccineLot();
        api.write(() -> lots.insert(lot),
                "Vaccine lot " + lot.lot() + " already exists.",
                "Manufacturer " + lot.manufacturer() + " does not exist.");
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{lot}").buildAndExpand(lot.lot()).toUri()).body(lot);
    }

    @PutMapping("/{lot}")
    public VaccineLot update(@PathVariable String lot, @RequestBody VaccineLotForm form) {
        form.setLot(lot);
        api.validate(form);
        VaccineLot updated = form.toVaccineLot();
        boolean found = api.write(() -> lots.update(updated), "",
                "Manufacturer " + updated.manufacturer() + " does not exist.");
        if (!found) {
            throw notFound(lot);
        }
        return updated;
    }

    @DeleteMapping("/{lot}")
    public ResponseEntity<Void> delete(@PathVariable String lot) {
        boolean deleted = api.write(() -> lots.delete(lot), "",
                "Lot " + lot + " has been used for vaccinations and cannot be deleted.");
        if (!deleted) {
            throw notFound(lot);
        }
        return ResponseEntity.noContent().build();
    }

    private static NotFoundException notFound(String lot) {
        return new NotFoundException("Vaccine lot " + lot + " not found.");
    }
}
