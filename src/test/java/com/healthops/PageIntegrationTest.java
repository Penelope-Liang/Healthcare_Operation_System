package com.healthops;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
class PageIntegrationTest {

    @Autowired
    MockMvc mvc;

    @ParameterizedTest
    @CsvSource({
            "/, Dashboard",
            "/patients, Patient directory",
            "/vaccinations, Vaccination records",
            "/vaccines, Vaccine inventory",
            "/clinics, Clinics",
            "/staff, Clinical staff",
            "/staff?role=doctor, Clinical staff",
    })
    void pagesRender(String path, String heading) throws Exception {
        mvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(heading)))
                .andExpect(content().string(containsString("aria-label=\"Primary navigation\"")));
    }

    @Test
    void dashboardShowsSeedMetrics() throws Exception {
        mvc.perform(get("/"))
                .andExpect(content().string(containsString("Pharmasave Division Pharmacy")))
                .andExpect(content().string(containsString("Doses by manufacturer")));
    }

    @Test
    void invalidPatientFormReopensDialogWithErrors() throws Exception {
        mvc.perform(post("/patients")
                        .param("ohip", "12")
                        .param("firstName", "Jane")
                        .param("lastName", "")
                        .param("dob", "1990-01-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients"))
                .andExpect(model().attributeHasFieldErrors("patientForm", "ohip", "lastName"))
                .andExpect(content().string(containsString("data-auto-open=\"true\"")))
                .andExpect(content().string(containsString("OHIP must use the format 0000-000-000.")));
    }

    @Test
    void validPatientFormRedirectsWithToast() throws Exception {
        mvc.perform(post("/patients")
                        .param("ohip", "7777-888-999")
                        .param("spouseOhip", "")
                        .param("firstName", "Page")
                        .param("lastName", "Patient")
                        .param("dob", "1985-06-15"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients"))
                .andExpect(flash().attribute("toast", "Page Patient was added."));

        mvc.perform(get("/patients")).andExpect(content().string(containsString("7777-888-999")));

        mvc.perform(post("/patients/7777-888-999/delete"))
                .andExpect(redirectedUrl("/patients"))
                .andExpect(flash().attribute("toast", containsString("removed")));
        mvc.perform(get("/patients")).andExpect(content().string(not(containsString("7777-888-999"))));
    }

    @Test
    void duplicateClinicShowsFieldError() throws Exception {
        mvc.perform(post("/clinics")
                        .param("name", "Rexall")
                        .param("street", "1 Main St")
                        .param("city", "Kingston")
                        .param("prov", "ON")
                        .param("pc", "K7L 1A1")
                        .param("operatingDate", "2026-01-01"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("A clinic with this name already exists.")));
    }

    @Test
    void deletingReferencedLotShowsErrorToast() throws Exception {
        mvc.perform(post("/vaccines/AB1234/delete"))
                .andExpect(redirectedUrl("/vaccines"))
                .andExpect(flash().attribute("toastError", containsString("used by vaccination records")));
    }

    @Test
    void staffPageGroupsMultiClinicAssignments() throws Exception {
        String html = mvc.perform(get("/staff")).andReturn().getResponse().getContentAsString();
        // Doctor XC546 works at two clinics but is listed once.
        org.assertj.core.api.Assertions.assertThat(html.split(">XC546<", -1)).hasSize(2);
    }
}
