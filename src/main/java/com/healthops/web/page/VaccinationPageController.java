package com.healthops.web.page;

import com.healthops.repository.ClinicRepository;
import com.healthops.repository.PatientRepository;
import com.healthops.repository.VaccinationRepository;
import com.healthops.repository.VaccineRepository;
import com.healthops.web.form.VaccinationForm;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vaccinations")
public class VaccinationPageController {

    private final VaccinationRepository vaccinations;
    private final PatientRepository patients;
    private final ClinicRepository clinics;
    private final VaccineRepository vaccines;

    public VaccinationPageController(VaccinationRepository vaccinations, PatientRepository patients,
                                     ClinicRepository clinics, VaccineRepository vaccines) {
        this.vaccinations = vaccinations;
        this.patients = patients;
        this.clinics = clinics;
        this.vaccines = vaccines;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("vaccinationForm", new VaccinationForm());
        return render(model);
    }

    @PostMapping
    public String create(@Valid @ModelAttribute VaccinationForm vaccinationForm, BindingResult result,
                         Model model, RedirectAttributes ra) {
        if (!result.hasErrors()) {
            try {
                vaccinations.insert(vaccinationForm.toVaccination());
                Flash.success(ra, "Vaccination recorded for " + vaccinationForm.getOhip() + ".");
                return "redirect:/vaccinations";
            } catch (DataIntegrityViolationException e) {
                result.reject("conflict", Flash.isDuplicate(e)
                        ? "This patient already has a vaccination record."
                        : "Unknown patient, clinic or vaccine lot.");
            }
        }
        model.addAttribute(Flash.OPEN_DIALOG, "vaccination-dialog");
        return render(model);
    }

    @PostMapping("/{ohip}/delete")
    public String delete(@PathVariable String ohip, RedirectAttributes ra) {
        vaccinations.delete(ohip);
        Flash.success(ra, "Vaccination record for " + ohip + " was removed.");
        return "redirect:/vaccinations";
    }

    private String render(Model model) {
        model.addAttribute("vaccinations", vaccinations.findDetails(Integer.MAX_VALUE));
        model.addAttribute("eligiblePatients", patients.findUnvaccinated());
        model.addAttribute("clinics", clinics.findAll());
        model.addAttribute("vaccines", vaccines.findAll());
        return "vaccinations";
    }
}
