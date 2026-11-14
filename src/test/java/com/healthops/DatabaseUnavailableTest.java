package com.healthops;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** With MySQL unreachable the app still starts, pages explain the problem and the API says 503. */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:mysql://127.0.0.1:1/covidDB?connectTimeout=500",
        "spring.datasource.hikari.connection-timeout=500",
})
@AutoConfigureMockMvc
class DatabaseUnavailableTest {

    @Autowired
    MockMvc mvc;

    @Test
    void pageShowsFriendlyMessage() throws Exception {
        mvc.perform(get("/patients"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string(containsString("Database is not connected")));
    }

    @Test
    void apiReturns503AndHealthReportsUnavailable() throws Exception {
        mvc.perform(get("/api/patients"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("Database is not connected."));
        mvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.database").value("unavailable"));
    }
}
