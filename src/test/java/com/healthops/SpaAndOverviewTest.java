package com.healthops;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
class SpaAndOverviewTest {

    @Autowired
    MockMvc mvc;

    @ParameterizedTest
    @ValueSource(strings = {"/patients", "/vaccinations", "/vaccines", "/clinics", "/staff"})
    void clientSideRoutesServeTheVueApp(String path) throws Exception {
        mvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    void dashboardSummarisesSeedData() throws Exception {
        mvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patients").value(6))
                .andExpect(jsonPath("$.vaccinated").value(6))
                .andExpect(jsonPath("$.clinics").value(6))
                .andExpect(jsonPath("$.nurses").value(6))
                .andExpect(jsonPath("$.doctors").value(6))
                .andExpect(jsonPath("$.dosesInInventory").value(12))
                .andExpect(jsonPath("$.dosesByManufacturer[0].manufacturer").value("Moderna"))
                .andExpect(jsonPath("$.recentVaccinations", hasSize(6)))
                .andExpect(jsonPath("$.recentVaccinations[0].date").value("2022-01-23"))
                .andExpect(jsonPath("$.clinicCoverage", hasSize(6)));
    }

    @Test
    void patientOverviewJoinsVaccination() throws Exception {
        mvc.perform(get("/api/patients/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.ohip == '1029-293-345')].manufacturer").value(hasItem("Astrazeneca")))
                .andExpect(jsonPath("$[?(@.ohip == '1029-293-345')].clinicName").value(hasItem("Pharmasave Division Pharmacy")));
    }

    @Test
    void clinicOverviewIncludesLotsAndStaff() throws Exception {
        mvc.perform(get("/api/clinics/overview"))
                .andExpect(jsonPath("$[?(@.clinic.name == 'Rexall')].lots[0]").value(hasItem("CD2345")))
                .andExpect(jsonPath("$[?(@.clinic.name == 'Rexall')].nurses").value(hasItem(1)));
    }

    @Test
    void vaccinationDetailsAndCompanies() throws Exception {
        mvc.perform(get("/api/vaccinations/details"))
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[0].firstName").value("Kitty"));
        mvc.perform(get("/api/companies"))
                .andExpect(jsonPath("$[*].name", hasItem("Pfizer")));
    }
}
