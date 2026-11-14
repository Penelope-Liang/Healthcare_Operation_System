package com.healthops.web.api;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    private final JdbcClient jdbc;

    public HealthController(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/api/health")
    public Map<String, String> health() {
        String database;
        try {
            jdbc.sql("SELECT 1").query(Integer.class).single();
            database = "connected";
        } catch (RuntimeException e) {
            database = "unavailable";
        }
        return Map.of("status", "ok", "database", database);
    }
}
