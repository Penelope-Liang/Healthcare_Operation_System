package com.healthops.web.page;

import com.healthops.repository.ClinicRepository;
import com.healthops.web.form.ClinicForm;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clinics")
public class ClinicPageController {

    private final ClinicRepository clinics;

    public ClinicPageController(ClinicRepository clinics) {
        this.clinics = clinics;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("clinicForm", new ClinicForm());
        return render(model);
    }

    @PostMapping
    public String create(@Valid @ModelAttribute ClinicForm clinicForm, BindingResult result,
                         Model model, RedirectAttributes ra) {
        if (!result.hasErrors()) {
            try {
                clinics.insert(clinicForm.toClinic());
                Flash.success(ra, clinicForm.getName() + " was added.");
                return "redirect:/clinics";
            } catch (DataIntegrityViolationException e) {
                result.rejectValue("name", "duplicate", "A clinic with this name already exists.");
            }
        }
        model.addAttribute(Flash.OPEN_DIALOG, "clinic-dialog");
        return render(model);
    }

    @PostMapping("/{name}/delete")
    public String delete(@PathVariable String name, RedirectAttributes ra) {
        try {
            clinics.delete(name);
            Flash.success(ra, name + " was removed.");
        } catch (DataIntegrityViolationException e) {
            Flash.error(ra, name + " still has vaccination records. Remove them first.");
        }
        return "redirect:/clinics";
    }

    private String render(Model model) {
        model.addAttribute("clinics", clinics.findOverview());
        return "clinics";
    }
}
