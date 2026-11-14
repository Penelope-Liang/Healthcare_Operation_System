package com.healthops.web.page;

import com.healthops.repository.DashboardRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardRepository dashboard;

    public DashboardController(DashboardRepository dashboard) {
        this.dashboard = dashboard;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("summary", dashboard.summary());
        return "dashboard";
    }
}
