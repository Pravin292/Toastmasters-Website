package com.rathinam.toastmasters.health.controller;

import com.rathinam.toastmasters.common.dto.ApiResponse;
import com.rathinam.toastmasters.health.dto.HealthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<ApiResponse<HealthResponse>> checkHealth() {
        HealthResponse healthInfo = new HealthResponse(
            "UP",
            "Rathinam Toastmasters Digital Platform Backend",
            "0.0.1-SNAPSHOT"
        );
        return ResponseEntity.ok(ApiResponse.success(healthInfo, "Backend service is operational"));
    }
}
