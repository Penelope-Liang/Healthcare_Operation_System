package com.healthops.web.page;

import com.healthops.domain.Role;
import com.healthops.domain.StaffMember;
import com.healthops.domain.Worker;
import com.healthops.repository.ClinicRepository;
import com.healthops.repository.WorkerRepository;
import com.healthops.web.form.WorkerForm;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/staff")
public class StaffPageController {

    private final WorkerRepository workers;
    private final ClinicRepository clinics;

    public StaffPageController(WorkerRepository workers, ClinicRepository clinics) {
        this.workers = workers;
        this.clinics = clinics;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String role, Model model) {
        WorkerForm form = new WorkerForm();
        form.setRole("nurse");
        model.addAttribute("workerForm", form);
        return render(role, model);
    }

    @PostMapping
    public String create(@Valid @ModelAttribute WorkerForm workerForm, BindingResult result,
                         Model model, RedirectAttributes ra) {
        if (!result.hasErrors()) {
            Worker worker = workerForm.toWorker();
            try {
                workers.insert(worker);
                Flash.success(ra, worker.fullName() + " was assigned to " + worker.clinicName() + ".");
                return "redirect:/staff";
            } catch (DataIntegrityViolationException e) {
                result.rejectValue("id", "duplicate", "A " + worker.role().key() + " with this ID already exists.");
            }
        }
        model.addAttribute(Flash.OPEN_DIALOG, "worker-dialog");
        return render(null, model);
    }

    @PostMapping("/{role}/{id}/delete")
    public String delete(@PathVariable String role, @PathVariable String id, RedirectAttributes ra) {
        workers.delete(Role.from(role), id);
        Flash.success(ra, "Staff member " + id + " was removed.");
        return "redirect:/staff";
    }

    private String render(String roleFilter, Model model) {
        Role role = parseRole(roleFilter);
        List<StaffMember> all = StaffMember.group(workers.findAll());
        model.addAttribute("staff", role == null ? all : all.stream().filter(m -> m.role() == role).toList());
        model.addAttribute("roleFilter", role == null ? "all" : role.key());
        model.addAttribute("nurseCount", all.stream().filter(m -> m.role() == Role.NURSE).count());
        model.addAttribute("doctorCount", all.stream().filter(m -> m.role() == Role.DOCTOR).count());
        model.addAttribute("clinics", clinics.findAll());
        return "staff";
    }

    private static Role parseRole(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Role.from(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
