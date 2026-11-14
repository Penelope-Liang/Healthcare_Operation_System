package com.healthops.web.page;

import com.healthops.repository.PatientRepository;
import com.healthops.web.form.PatientForm;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/patients")
public class PatientPageController {

    private final PatientRepository patients;

    public PatientPageController(PatientRepository patients) {
        this.patients = patients;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("patientForm", new PatientForm());
        return render(model);
    }

    @PostMapping
    public String create(@Valid @ModelAttribute PatientForm patientForm, BindingResult result,
                         Model model, RedirectAttributes ra) {
        if (!result.hasErrors()) {
            try {
                patients.insert(patientForm.toPatient());
                Flash.success(ra, patientForm.getFirstName() + " " + patientForm.getLastName() + " was added.");
                return "redirect:/patients";
            } catch (DataIntegrityViolationException e) {
                result.rejectValue("ohip", "duplicate", "A patient with this OHIP already exists.");
            }
        }
        model.addAttribute(Flash.OPEN_DIALOG, "patient-dialog");
        return render(model);
    }

    @PostMapping("/{ohip}/delete")
    public String delete(@PathVariable String ohip, RedirectAttributes ra) {
        try {
            patients.delete(ohip);
            Flash.success(ra, "Patient " + ohip + " was removed.");
        } catch (DataIntegrityViolationException e) {
            Flash.error(ra, "Patient " + ohip + " still has a vaccination or spouse record. Remove it first.");
        }
        return "redirect:/patients";
    }

    private String render(Model model) {
        model.addAttribute("patients", patients.findOverview());
        return "patients";
    }
}
