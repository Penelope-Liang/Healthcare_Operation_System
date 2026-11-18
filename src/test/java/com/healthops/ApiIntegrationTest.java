package com.healthops;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
class ApiIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper json;

    private ResultActions postJson(String url, String body) throws Exception {
        return mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(body));
    }

    private ResultActions putJson(String url, String body) throws Exception {
        return mvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(body));
    }

    private JsonNode body(ResultActions result) throws Exception {
        return json.readTree(result.andReturn().getResponse().getContentAsString());
    }

    @Test
    void healthReportsDatabaseConnection() throws Exception {
        mvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.database").value("connected"));
    }

    @Test
    void patientCrudWithSpouse() throws Exception {
        postJson("/api/patients", """
                {"ohip":"5555-111-222","firstName":"Mary-Kate","lastName":"O'Neil","dateOfBirth":"1994-04-14","spouseOhip":"5555-111-999"}""")
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/patients/5555-111-222"))
                .andExpect(jsonPath("$.firstName").value("Mary-Kate"));

        mvc.perform(get("/api/patients/5555-111-222"))
                .andExpect(jsonPath("$.lastName").value("O'Neil"))
                .andExpect(jsonPath("$.spouseOhip").value("5555-111-999"));

        // Clearing the spouse removes the spouse row.
        putJson("/api/patients/5555-111-222", """
                {"firstName":"Mary","lastName":"Neil","dateOfBirth":"1994-04-15","spouseOhip":"  "}""")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dateOfBirth").value("1994-04-15"));
        mvc.perform(get("/api/patients/5555-111-222")).andExpect(jsonPath("$.spouseOhip").doesNotExist());

        mvc.perform(delete("/api/patients/5555-111-222")).andExpect(status().isNoContent());
        mvc.perform(get("/api/patients/5555-111-222"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Patient 5555-111-222 not found."));
    }

    @Test
    void seededSpousesComeFromTheSpouseTable() throws Exception {
        mvc.perform(get("/api/patients/3424-091-654")).andExpect(jsonPath("$.spouseOhip").doesNotExist());
        mvc.perform(get("/api/patients/1029-293-345")).andExpect(jsonPath("$.spouseOhip").value("2342-543-798"));
    }

    @Test
    void patientValidationReportsEachField() throws Exception {
        postJson("/api/patients", """
                {"ohip":"123","firstName":"Jane1","dateOfBirth":"2999-01-01"}""")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.fieldErrors.ohip").value("OHIP must use the format 0000-000-000."))
                .andExpect(jsonPath("$.fieldErrors.firstName").value(containsString("First name must be 1-30 letters")))
                .andExpect(jsonPath("$.fieldErrors.lastName").value("Last name is required."))
                .andExpect(jsonPath("$.fieldErrors.dateOfBirth").value("Date of birth must be in the past."));

        postJson("/api/patients", """
                {"ohip":"5555-000-111","firstName":"Same","lastName":"Person","dateOfBirth":"1990-01-01","spouseOhip":"5555-000-111"}""")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.fieldErrors.spouseDifferent").exists());
    }

    @Test
    void duplicatePatientIsConflict() throws Exception {
        postJson("/api/patients", """
                {"ohip":"1029-293-345","firstName":"Amy","lastName":"White","dateOfBirth":"1990-01-23"}""")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("A patient with OHIP 1029-293-345 already exists."));
    }

    @Test
    void patientsWithVaccinationHistoryCannotBeDeleted() throws Exception {
        mvc.perform(delete("/api/patients/0903-434-120"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(containsString("vaccination records")));
    }

    @Test
    void malformedJsonIsBadRequest() throws Exception {
        postJson("/api/patients", "{not json").andExpect(status().isBadRequest());
        postJson("/api/patients", """
                {"ohip":"5555-111-333","firstName":"A","lastName":"B","dateOfBirth":"1990-02-31"}""")
                .andExpect(status().isBadRequest());
    }

    @Test
    void clinicCrud() throws Exception {
        String body = """
                {"name":"Api Clinic","street":"100 Api Street","city":"Kingston","province":"ON","postalCode":"K7L 1A1","operatingDate":"2026-08-02"}""";
        postJson("/api/clinics", body).andExpect(status().isCreated());
        postJson("/api/clinics", body).andExpect(status().isConflict());

        mvc.perform(get("/api/clinics/Api Clinic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postalCode").value("K7L 1A1"))
                .andExpect(jsonPath("$.operatingDate").value("2026-08-02"));

        putJson("/api/clinics/Api Clinic", """
                {"street":"200 Api Street","city":"Kingston","province":"ON","postalCode":"K7L 1A1","operatingDate":"2026-09-01"}""")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.street").value("200 Api Street"));

        mvc.perform(delete("/api/clinics/Api Clinic")).andExpect(status().isNoContent());
        mvc.perform(delete("/api/clinics/Api Clinic")).andExpect(status().isNotFound());
    }

    @Test
    void vaccineLotValidation() throws Exception {
        postJson("/api/vaccines", """
                {"lot":"WAY-TOO-LONG-LOT","manufacturer":"Pfizer","productionDate":"2026-08-02","expiryDate":"2026-01-01","doses":-1}""")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.fieldErrors.lot").value("Lot must be 1-12 letters or numbers."))
                .andExpect(jsonPath("$.fieldErrors.doses").value("Doses must be at least 0."))
                .andExpect(jsonPath("$.fieldErrors.expiryAfterProduction").value("Expiry date must be after the production date."));
    }

    @Test
    void lotShipmentAndMultiDoseVaccinationFlow() throws Exception {
        postJson("/api/vaccines", """
                {"lot":"ZX900100","manufacturer":"Pfizer","productionDate":"2026-08-02","expiryDate":"2027-08-02","doses":20}""")
                .andExpect(status().isCreated());
        postJson("/api/vaccines", """
                {"lot":"ZX9002","manufacturer":"Nobody Inc","productionDate":"2026-08-02","expiryDate":"2027-08-02","doses":20}""")
                .andExpect(status().isConflict());

        postJson("/api/shipments", """
                {"lot":"ZX900100","clinicName":"Rexall"}""")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.manufacturer").value("Pfizer"));
        mvc.perform(get("/api/shipments/ZX900100/Rexall")).andExpect(status().isOk());

        postJson("/api/patients", """
                {"ohip":"5555-222-333","firstName":"Shot","lastName":"Taker","dateOfBirth":"1980-01-01"}""")
                .andExpect(status().isCreated());

        long first = body(postJson("/api/vaccinations", """
                {"ohip":"5555-222-333","clinicName":"Rexall","lot":"ZX900100","date":"2026-08-03","time":"09:30"}""")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.time").value("09:30"))).get("id").asLong();
        long second = body(postJson("/api/vaccinations", """
                {"ohip":"5555-222-333","clinicName":"Rexall","lot":"ZX900100","date":"2026-11-03","time":"10:00"}""")
                .andExpect(status().isCreated())).get("id").asLong();

        mvc.perform(get("/api/vaccinations/details"))
                .andExpect(jsonPath("$[?(@.id == " + second + ")].doseNumber").value(hasItem(2)));
        mvc.perform(get("/api/patients/overview"))
                .andExpect(jsonPath("$[?(@.ohip == '5555-222-333')].doses").value(hasItem(2)))
                .andExpect(jsonPath("$[?(@.ohip == '5555-222-333')].lastDoseDate").value(hasItem("2026-11-03")));

        putJson("/api/vaccinations/" + first, """
                {"ohip":"5555-222-333","clinicName":"Rexall","lot":"ZX900100","date":"2026-08-04","time":"25:00"}""")
                .andExpect(status().isBadRequest());
        putJson("/api/vaccinations/" + first, """
                {"ohip":"5555-222-333","clinicName":"Nowhere","lot":"ZX900100","date":"2026-08-04","time":"11:15"}""")
                .andExpect(status().isConflict());
        putJson("/api/vaccinations/" + first, """
                {"ohip":"5555-222-333","clinicName":"Rexall","lot":"ZX900100","date":"2026-08-04","time":"11:15"}""")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2026-08-04"));

        mvc.perform(delete("/api/vaccines/ZX900100")).andExpect(status().isConflict());
        mvc.perform(delete("/api/vaccinations/" + first)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/vaccinations/" + second)).andExpect(status().isNoContent());
        mvc.perform(get("/api/vaccinations/" + second)).andExpect(status().isNotFound());
        mvc.perform(delete("/api/vaccines/ZX900100")).andExpect(status().isNoContent());
        mvc.perform(get("/api/shipments/ZX900100/Rexall")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/patients/5555-222-333")).andExpect(status().isNoContent());
    }

    @Test
    void staffCrudWithSeveralCredentialsAndClinics() throws Exception {
        postJson("/api/staff", """
                {"id":"ZQ1","role":"nurse","firstName":"Api","lastName":"Nurse","credentials":["RN","BSN","RN"],"clinics":["Rexall"]}""")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("nurse"))
                .andExpect(jsonPath("$.credentials.length()").value(2));
        postJson("/api/staff", """
                {"id":"ZQ1","role":"doctor","firstName":"Dup","lastName":"Licate","credentials":["MD"]}""")
                .andExpect(status().isConflict());

        putJson("/api/staff/ZQ1", """
                {"role":"doctor","firstName":"Moved","lastName":"Doctor","credentials":["MD"],"clinics":["Rexall","Shoppers Drug Mart"]}""")
                .andExpect(status().isOk());
        mvc.perform(get("/api/staff/ZQ1"))
                .andExpect(jsonPath("$.role").value("doctor"))
                .andExpect(jsonPath("$.credentials[0]").value("MD"))
                .andExpect(jsonPath("$.clinics.length()").value(2));
        mvc.perform(get("/api/staff").param("role", "doctor"))
                .andExpect(jsonPath("$[*].id", hasItem("ZQ1")));

        mvc.perform(delete("/api/staff/ZQ1")).andExpect(status().isNoContent());
        mvc.perform(get("/api/staff/ZQ1")).andExpect(status().isNotFound());
    }

    @Test
    void staffValidationAndUnknownClinic() throws Exception {
        postJson("/api/staff", """
                {"id":"ZZ1","role":"surgeon","firstName":"A","lastName":"B","credentials":[]}""")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.fieldErrors.role").value("Role must be nurse or doctor."))
                .andExpect(jsonPath("$.fieldErrors.credentials").value("At least one credential is required."));
        mvc.perform(get("/api/staff").param("role", "surgeon")).andExpect(status().isUnprocessableEntity());

        postJson("/api/staff", """
                {"id":"ZZ2","role":"nurse","firstName":"A","lastName":"B","credentials":["RN"],"clinics":["Nowhere"]}""")
                .andExpect(status().isConflict());
        // The insert ran in one transaction, so no half-created staff row is left behind.
        mvc.perform(get("/api/staff/ZZ2")).andExpect(status().isNotFound());
    }

    @Test
    void vaccineLotReadUpdateAndNotFound() throws Exception {
        mvc.perform(get("/api/vaccines/AB1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.manufacturer").value("Astrazeneca"))
                .andExpect(jsonPath("$.expiryDate").value("2022-08-01"));
        mvc.perform(get("/api/vaccines"))
                .andExpect(jsonPath("$[*].lot", hasItem("KL6789")));

        postJson("/api/vaccines", """
                {"lot":"UPD001","manufacturer":"Moderna","productionDate":"2026-01-01","expiryDate":"2026-12-31","doses":5}""")
                .andExpect(status().isCreated());
        putJson("/api/vaccines/UPD001", """
                {"manufacturer":"Pfizer","productionDate":"2026-01-01","expiryDate":"2027-06-30","doses":12}""")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doses").value(12));
        mvc.perform(get("/api/vaccines/UPD001")).andExpect(jsonPath("$.manufacturer").value("Pfizer"));
        putJson("/api/vaccines/UPD001", """
                {"manufacturer":"Nobody Inc","productionDate":"2026-01-01","expiryDate":"2027-06-30","doses":12}""")
                .andExpect(status().isConflict());
        putJson("/api/vaccines/UPD001", """
                {"manufacturer":"Pfizer","productionDate":"2026-01-01","expiryDate":"2025-06-30","doses":12}""")
                .andExpect(status().isUnprocessableEntity());
        mvc.perform(delete("/api/vaccines/UPD001")).andExpect(status().isNoContent());

        mvc.perform(get("/api/vaccines/NOPE01")).andExpect(status().isNotFound());
        putJson("/api/vaccines/NOPE01", """
                {"manufacturer":"Pfizer","productionDate":"2026-01-01","expiryDate":"2027-06-30","doses":1}""")
                .andExpect(status().isNotFound());
        mvc.perform(delete("/api/vaccines/NOPE01")).andExpect(status().isNotFound());
    }

    @Test
    void listsAndMissingRecordsAcrossResources() throws Exception {
        mvc.perform(get("/api/shipments")).andExpect(jsonPath("$[*].lot", hasItem("AB1234")));
        mvc.perform(get("/api/shipments/AB1234/Nowhere")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/shipments/AB1234/Nowhere")).andExpect(status().isNotFound());
        postJson("/api/shipments", """
                {"lot":"NOPE01","clinicName":"Rexall"}""").andExpect(status().isConflict());
        postJson("/api/shipments", "{}")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.fieldErrors.lot").value("Lot is required."))
                .andExpect(jsonPath("$.fieldErrors.clinicName").value("Clinic is required."));

        mvc.perform(get("/api/vaccinations")).andExpect(jsonPath("$[*].ohip", hasItem("1029-293-345")));
        mvc.perform(get("/api/patients")).andExpect(jsonPath("$[*].ohip", hasItem("1029-293-345")));
        mvc.perform(get("/api/clinics")).andExpect(jsonPath("$[*].name", hasItem("Rexall")));
        mvc.perform(get("/api/clinics/Nowhere")).andExpect(status().isNotFound());
        mvc.perform(get("/api/staff")).andExpect(jsonPath("$[*].id", hasItem("QW102")));

        putJson("/api/patients/0000-000-000", """
                {"firstName":"No","lastName":"One","dateOfBirth":"1990-01-01"}""").andExpect(status().isNotFound());
        putJson("/api/clinics/Nowhere", """
                {"street":"1 Main","city":"Kingston","province":"ON","postalCode":"K7L 1A1","operatingDate":"2026-01-01"}""")
                .andExpect(status().isNotFound());
        putJson("/api/staff/NOPE1", """
                {"role":"nurse","firstName":"No","lastName":"One","credentials":["RN"]}""").andExpect(status().isNotFound());
        mvc.perform(delete("/api/staff/NOPE1")).andExpect(status().isNotFound());
        putJson("/api/vaccinations/999999", """
                {"ohip":"1029-293-345","clinicName":"Rexall","lot":"CD2345","date":"2026-01-01","time":"10:00"}""")
                .andExpect(status().isNotFound());
        mvc.perform(delete("/api/vaccinations/999999")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/patients/0000-000-000")).andExpect(status().isNotFound());
        mvc.perform(post("/api/health")).andExpect(status().isMethodNotAllowed());
    }
}
