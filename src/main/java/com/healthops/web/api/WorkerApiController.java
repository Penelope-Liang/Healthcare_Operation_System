package com.healthops.web.api;

import com.healthops.domain.Role;
import com.healthops.domain.Worker;
import com.healthops.repository.WorkerRepository;
import com.healthops.web.NotFoundException;
import com.healthops.web.form.WorkerForm;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/workers")
public class WorkerApiController {

    private final WorkerRepository workers;
    private final ApiSupport api;

    public WorkerApiController(WorkerRepository workers, ApiSupport api) {
        this.workers = workers;
        this.api = api;
    }

    @GetMapping
    public List<Worker> list(@RequestParam(required = false) String role) {
        return role == null ? workers.findAll() : workers.findByRole(Role.from(role));
    }

    @GetMapping("/{role}/{id}")
    public Worker get(@PathVariable String role, @PathVariable String id) {
        return workers.findById(Role.from(role), id).orElseThrow(() -> notFound(role, id));
    }

    @PostMapping
    public ResponseEntity<Worker> create(@Valid @RequestBody WorkerForm form) {
        Worker worker = form.toWorker();
        api.write(() -> workers.insert(worker),
                "A " + worker.role().key() + " with ID " + worker.id() + " already exists.",
                "Clinic " + worker.clinicName() + " does not exist.");
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{role}/{id}").buildAndExpand(worker.role().key(), worker.id()).toUri()).body(worker);
    }

    @PutMapping("/{role}/{id}")
    public Worker update(@PathVariable String role, @PathVariable String id, @RequestBody WorkerForm form) {
        form.setRole(Role.from(role).key());
        form.setId(id);
        api.validate(form);
        Worker worker = form.toWorker();
        boolean updated = api.write(() -> workers.update(worker), "",
                "Clinic " + worker.clinicName() + " does not exist.");
        if (!updated) {
            throw notFound(role, id);
        }
        return worker;
    }

    @DeleteMapping("/{role}/{id}")
    public ResponseEntity<Void> delete(@PathVariable String role, @PathVariable String id) {
        if (!workers.delete(Role.from(role), id)) {
            throw notFound(role, id);
        }
        return ResponseEntity.noContent().build();
    }

    private static NotFoundException notFound(String role, String id) {
        return new NotFoundException("No " + role.toLowerCase() + " with ID " + id + ".");
    }
}
