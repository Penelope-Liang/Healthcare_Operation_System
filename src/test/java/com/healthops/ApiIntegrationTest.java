package com.healthops;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
class ApiIntegrationTest {

    @Autowired
    MockMvc mvc;

    private ResultActions postJson(String url, String json) throws Exception {
        return mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private ResultActions putJson(String url, String json) throws Exception {
        return mvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    void healthReportsDatabaseConnection() throws Exception {
        mvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.database").value("connected"));
    }

    @Test
    void patientCrud() throws Exception {
        postJson("/api/patients", """
                {"ohip":"5555-111-222","firstName":"Api","lastName":"Patient","dob":"1994-04-14"}""")
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/patients/5555-111-222"))
                .andExpect(jsonPath("$.ohip").value("5555-111-222"))
                .andExpect(jsonPath("$.spouseOhip").doesNotExist());

        mvc.perform(get("/api/patients/5555-111-222"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Api"));

        putJson("/api/patients/5555-111-222", """
                {"firstName":"Updated","lastName":"Patient","dob":"1994-04-15","spouseOhip":"  "}""")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.dob").value("1994-04-15"));

        mvc.perform(get("/api/patients")).andExpect(jsonPath("$[*].ohip", hasItem("5555-111-222")));

        mvc.perform(delete("/api/patients/5555-111-222")).andExpect(status().isNoContent());
        mvc.perform(get("/api/patients/5555-111-222"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Patient 5555-111-222 not found."));
    }

    @Test
    void seededSpouseStoredAsTextNullIsReturnedAsNull() throws Exception {
        mvc.perform(get("/api/patients/3424-091-654")).andExpect(jsonPath("$.spouseOhip").doesNotExist());
        mvc.perform(get("/api/patients/1029-293-345")).andExpect(jsonPath("$.spouseOhip").value("2342-543-798"));
    }

    @Test
    void patientValidationReportsEachField() throws Exception {
        postJson("/api/patients", """
                {"ohip":"123","firstName":"Jane1","dob":"2999-01-01"}""")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.fieldErrors.ohip").value("OHIP must use the format 0000-000-000."))
                .andExpect(jsonPath("$.fieldErrors.firstName").value("First name must be 1-30 letters."))
                .andExpect(jsonPath("$.fieldErrors.lastName").value("Last name is required."))
                .andExpect(jsonPath("$.fieldErrors.dob").value("Date of birth must be in the past."));
    }

    @Test
    void duplicatePatientIsConflict() throws Exception {
        postJson("/api/patients", """
                {"ohip":"1029-293-345","firstName":"Amy","lastName":"White","dob":"1990-01-23"}""")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("A patient with OHIP 1029-293-345 already exists."));
    }

    @Test
    void deletingPatientWithVaccinationIsConflict() throws Exception {
        mvc.perform(delete("/api/patients/0903-434-120"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("vaccination")));
    }

    @Test
    void malformedJsonIsBadRequest() throws Exception {
        postJson("/api/patients", "{not json").andExpect(status().isBadRequest());
        postJson("/api/patients", """
                {"ohip":"5555-111-333","firstName":"A","lastName":"B","dob":"1990-02-31"}""")
                .andExpect(status().isBadRequest());
    }

    @Test
    void clinicCrud() throws Exception {
        String body = """
                {"name":"Api Clinic","street":"100 Api Street","city":"Kingston","prov":"ON","pc":"K7L 1A1","operatingDate":"2026-08-02"}""";
        postJson("/api/clinics", body).andExpect(status().isCreated());
        postJson("/api/clinics", body).andExpect(status().isConflict());

        mvc.perform(get("/api/clinics/Api Clinic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operatingDate").value("2026-08-02"));

        putJson("/api/clinics/Api Clinic", """
                {"street":"200 Api Street","city":"Kingston","prov":"ON","pc":"K7L 1A1","operatingDate":"2026-09-01"}""")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.street").value("200 Api Street"));

        mvc.perform(delete("/api/clinics/Api Clinic")).andExpect(status().isNoContent());
        mvc.perform(delete("/api/clinics/Api Clinic")).andExpect(status().isNotFound());
    }

    @Test
    void vaccineLotValidation() throws Exception {
        postJson("/api/vaccines", """
                {"lot":"TOOLONG1","companyName":"Pfizer","production":"2026-08-02","expiry":"2026-01-01","doses":-1}""")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.fieldErrors.lot").value("Lot must be 1-6 letters or numbers."))
                .andExpect(jsonPath("$.fieldErrors.doses").value("Doses must be at least 0."))
                .andExpect(jsonPath("$.fieldErrors.expiryAfterProduction").value("Expiry date must be after the production date."));
    }

    @Test
    void vaccineShipmentAndVaccinationFlow() throws Exception {
        postJson("/api/vaccines", """
                {"lot":"ZX9001","companyName":"Pfizer","production":"2026-08-02","expiry":"2027-08-02","doses":20}""")
                .andExpect(status().isCreated());
        postJson("/api/vaccines", """
                {"lot":"ZX9002","companyName":"Nobody Inc","production":"2026-08-02","expiry":"2027-08-02","doses":20}""")
                .andExpect(status().isConflict());

        postJson("/api/shipments", """
                {"lot":"ZX9001","clinic":"Rexall"}""")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.companyName").value("Pfizer"));
        mvc.perform(get("/api/shipments/ZX9001/Rexall")).andExpect(status().isOk());

        postJson("/api/patients", """
                {"ohip":"5555-222-333","firstName":"Shot","lastName":"Taker","dob":"1980-01-01"}""")
                .andExpect(status().isCreated());
        postJson("/api/vaccinations", """
                {"ohip":"5555-222-333","clinicName":"Rexall","lot":"ZX9001","date":"2026-08-03","time":"09:30"}""")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.time").value("09:30"));
        postJson("/api/vaccinations", """
                {"ohip":"5555-222-333","clinicName":"Rexall","lot":"ZX9001","date":"2026-08-03","time":"09:30"}""")
                .andExpect(status().isConflict());

        putJson("/api/vaccinations/5555-222-333", """
                {"clinicName":"Rexall","lot":"ZX9001","date":"2026-08-04","time":"25:00"}""")
                .andExpect(status().isBadRequest());
        putJson("/api/vaccinations/5555-222-333", """
                {"clinicName":"Rexall","lot":"ZX9001","date":"2026-08-04","time":"11:15"}""")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2026-08-04"));

        mvc.perform(delete("/api/vaccines/ZX9001")).andExpect(status().isConflict());
        mvc.perform(delete("/api/vaccinations/5555-222-333")).andExpect(status().isNoContent());
        mvc.perform(delete("/api/shipments/ZX9001/Rexall")).andExpect(status().isNoContent());
        mvc.perform(delete("/api/vaccines/ZX9001")).andExpect(status().isNoContent());
        mvc.perform(delete("/api/patients/5555-222-333")).andExpect(status().isNoContent());
    }

    @Test
    void workerCrudForBothRoles() throws Exception {
        for (String role : new String[]{"nurse", "doctor"}) {
            postJson("/api/workers", """
                    {"role":"%s","id":"ZQ%s","firstName":"Api","lastName":"Worker","credential":"RN","clinicName":"Rexall"}"""
                    .formatted(role, role.charAt(0)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.role").value(role));

            String url = "/api/workers/" + role + "/ZQ" + role.charAt(0);
            putJson(url, """
                    {"firstName":"Moved","lastName":"Worker","credential":"BSN","clinicName":"Shoppers Drug Mart"}""")
                    .andExpect(status().isOk());
            mvc.perform(get(url))
                    .andExpect(jsonPath("$.firstName").value("Moved"))
                    .andExpect(jsonPath("$.credential").value("BSN"))
                    .andExpect(jsonPath("$.clinicName").value("Shoppers Drug Mart"));
            mvc.perform(delete(url)).andExpect(status().isNoContent());
            mvc.perform(get(url)).andExpect(status().isNotFound());
        }
    }

    @Test
    void workerWithUnknownRoleOrClinicIsRejected() throws Exception {
        postJson("/api/workers", """
                {"role":"surgeon","id":"ZZ1","firstName":"A","lastName":"B","credential":"MD","clinicName":"Rexall"}""")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.fieldErrors.role").value("Role must be nurse or doctor."));
        mvc.perform(get("/api/workers").param("role", "surgeon")).andExpect(status().isUnprocessableEntity());
        postJson("/api/workers", """
                {"role":"nurse","id":"ZZ2","firstName":"A","lastName":"B","credential":"RN","clinicName":"Nowhere"}""")
                .andExpect(status().isConflict());
        mvc.perform(get("/api/workers/nurse/ZZ2")).andExpect(status().isNotFound());
    }
}
