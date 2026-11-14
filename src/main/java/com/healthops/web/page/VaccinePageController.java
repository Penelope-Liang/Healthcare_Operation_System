package com.healthops.web.page;

import com.healthops.repository.ClinicRepository;
import com.healthops.repository.ShipmentRepository;
import com.healthops.repository.VaccineRepository;
import com.healthops.web.form.ShipmentForm;
import com.healthops.web.form.VaccineForm;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vaccines")
public class VaccinePageController {

    private final VaccineRepository vaccines;
    private final ShipmentRepository shipments;
    private final ClinicRepository clinics;

    public VaccinePageController(VaccineRepository vaccines, ShipmentRepository shipments, ClinicRepository clinics) {
        this.vaccines = vaccines;
        this.shipments = shipments;
        this.clinics = clinics;
    }

    @GetMapping
    public String list(Model model) {
        return render(model);
    }

    @PostMapping
    public String createLot(@Valid @ModelAttribute VaccineForm vaccineForm, BindingResult result,
                            Model model, RedirectAttributes ra) {
        if (!result.hasErrors()) {
            try {
                vaccines.insert(vaccineForm.toVaccine());
                Flash.success(ra, "Lot " + vaccineForm.getLot() + " was added to inventory.");
                return "redirect:/vaccines";
            } catch (DataIntegrityViolationException e) {
                result.rejectValue("lot", "duplicate", "This lot already exists.");
            }
        }
        model.addAttribute(Flash.OPEN_DIALOG, "vaccine-dialog");
        return render(model);
    }

    @PostMapping("/{lot}/delete")
    public String deleteLot(@PathVariable String lot, RedirectAttributes ra) {
        try {
            vaccines.delete(lot);
            Flash.success(ra, "Lot " + lot + " was removed.");
        } catch (DataIntegrityViolationException e) {
            Flash.error(ra, "Lot " + lot + " is used by vaccination records. Remove them first.");
        }
        return "redirect:/vaccines";
    }

    @PostMapping("/shipments")
    public String assignShipment(@Valid @ModelAttribute ShipmentForm shipmentForm, BindingResult result,
                                 Model model, RedirectAttributes ra) {
        if (!result.hasErrors()) {
            try {
                shipments.insert(shipmentForm.getLot(), shipmentForm.getClinic());
                Flash.success(ra, "Lot " + shipmentForm.getLot() + " is shipping to " + shipmentForm.getClinic() + ".");
                return "redirect:/vaccines";
            } catch (DataIntegrityViolationException e) {
                result.reject("conflict", Flash.isDuplicate(e)
                        ? "This lot is already shipped to that clinic."
                        : "Unknown lot or clinic.");
            }
        }
        model.addAttribute(Flash.OPEN_DIALOG, "shipment-dialog");
        return render(model);
    }

    @PostMapping("/shipments/delete")
    public String deleteShipment(@RequestParam String lot, @RequestParam String clinic, RedirectAttributes ra) {
        shipments.delete(lot, clinic);
        Flash.success(ra, "Shipment of " + lot + " to " + clinic + " was cancelled.");
        return "redirect:/vaccines";
    }

    private String render(Model model) {
        if (!model.containsAttribute("vaccineForm")) {
            model.addAttribute("vaccineForm", new VaccineForm());
        }
        if (!model.containsAttribute("shipmentForm")) {
            model.addAttribute("shipmentForm", new ShipmentForm());
        }
        model.addAttribute("vaccines", vaccines.findAll());
        model.addAttribute("shipments", shipments.findAll());
        model.addAttribute("companies", vaccines.findCompanies());
        model.addAttribute("clinics", clinics.findAll());
        return "vaccines";
    }
}
