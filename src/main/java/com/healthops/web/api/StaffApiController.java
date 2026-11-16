package com.healthops.web.api;

import com.healthops.domain.Role;
import com.healthops.domain.StaffMember;
import com.healthops.repository.StaffRepository;
import com.healthops.web.NotFoundException;
import com.healthops.web.form.StaffForm;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffApiController {

    private final StaffRepository staff;
    private final ApiSupport api;

    public StaffApiController(StaffRepository staff, ApiSupport api) {
        this.staff = staff;
        this.api = api;
    }

    @GetMapping
    public List<StaffMember> list(@RequestParam(required = false) String role) {
        return role == null ? staff.findAll() : staff.findByRole(Role.from(role));
    }

    @GetMapping("/{id}")
    public StaffMember get(@PathVariable String id) {
        return staff.findById(id).orElseThrow(() -> notFound(id));
    }

    @PostMapping
    public ResponseEntity<StaffMember> create(@Valid @RequestBody StaffForm form) {
        StaffMember member = form.toStaffMember();
        api.write(() -> staff.insert(member),
                "A staff member with ID " + member.id() + " already exists.",
                "One of the clinics does not exist.");
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(member.id()).toUri()).body(member);
    }

    @PutMapping("/{id}")
    public StaffMember update(@PathVariable String id, @RequestBody StaffForm form) {
        form.setId(id);
        api.validate(form);
        StaffMember member = form.toStaffMember();
        boolean found = api.write(() -> staff.update(member), "", "One of the clinics does not exist.");
        if (!found) {
            throw notFound(id);
        }
        return member;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!staff.delete(id)) {
            throw notFound(id);
        }
        return ResponseEntity.noContent().build();
    }

    private static NotFoundException notFound(String id) {
        return new NotFoundException("Staff member " + id + " not found.");
    }
}
