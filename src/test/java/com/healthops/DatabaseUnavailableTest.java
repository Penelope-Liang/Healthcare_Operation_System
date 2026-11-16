package com.healthops;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** With MySQL unreachable the app still starts and the API answers 503 (the Vue app shows a help card). */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:mysql://127.0.0.1:1/healthops?connectTimeout=500",
        "spring.datasource.hikari.connection-timeout=500",
})
@AutoConfigureMockMvc
class DatabaseUnavailableTest {

    @Autowired
    MockMvc mvc;

    @Test
    void frontendRoutesStillLoad() throws Exception {
        mvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    void apiReturns503AndHealthReportsUnavailable() throws Exception {
        mvc.perform(get("/api/dashboard"))
                .andExpect(status().isServiceUnavailable());
        mvc.perform(get("/api/patients"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("Database is not connected."));
        mvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.database").value("unavailable"));
    }
}
