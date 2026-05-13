package com.petcarebackend.controller;

import com.petcarebackend.dto.ApiResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final JdbcTemplate jdbcTemplate;

    public TestController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/test/db")
    public ApiResponse<String> testDb() {
        String databaseName = jdbcTemplate.queryForObject("select current_database()", String.class);
        return ApiResponse.success("Database connection is active.", databaseName);
    }
}
