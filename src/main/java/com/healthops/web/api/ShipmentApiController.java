package com.healthops.web.api;

import com.healthops.domain.Shipment;
import com.healthops.repository.ShipmentRepository;
import com.healthops.web.NotFoundException;
import com.healthops.web.form.ShipmentForm;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentApiController {

    private final ShipmentRepository shipments;
    private final ApiSupport api;

    public ShipmentApiController(ShipmentRepository shipments, ApiSupport api) {
        this.shipments = shipments;
        this.api = api;
    }

    @GetMapping
    public List<Shipment> list() {
        return shipments.findAll();
    }

    @GetMapping("/{lot}/{clinic}")
    public Shipment get(@PathVariable String lot, @PathVariable String clinic) {
        return shipments.find(lot, clinic).orElseThrow(() -> notFound(lot, clinic));
    }

    @PostMapping
    public ResponseEntity<Shipment> create(@Valid @RequestBody ShipmentForm form) {
        api.write(() -> shipments.insert(form.getLot(), form.getClinicName()),
                "Lot " + form.getLot() + " is already shipped to " + form.getClinicName() + ".",
                "Unknown lot or clinic.");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(shipments.find(form.getLot(), form.getClinicName()).orElseThrow());
    }

    @DeleteMapping("/{lot}/{clinic}")
    public ResponseEntity<Void> delete(@PathVariable String lot, @PathVariable String clinic) {
        if (!shipments.delete(lot, clinic)) {
            throw notFound(lot, clinic);
        }
        return ResponseEntity.noContent().build();
    }

    private static NotFoundException notFound(String lot, String clinic) {
        return new NotFoundException("No shipment of lot " + lot + " to " + clinic + ".");
    }
}
