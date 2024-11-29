package com.example.jasperrepo.controller;

import com.example.jasperrepo.config.ReportConfig;
import com.example.jasperrepo.service.JReportService;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

    private final JReportService jReportService;
    private final ReportConfig reportConfig;  // Inject the configuration component

    @PostMapping("/{name}")
    public void generateReport(@PathVariable String name, @RequestBody Map<String, Object> requestBody, HttpServletResponse response) throws IOException {
        try {
            // Check if the name exists in the config
            if (reportConfig.getConfig().containsKey(name)) {
                // Get the expected structure for the report from the config
                Map<String, Object> expectedStructure = (Map<String, Object>) reportConfig.getConfig().get(name);

                // Validate the request body against the expected structure
                if (validateRequestStructure(requestBody, expectedStructure)) {
                    // Pass the report data directly to the service
                    jReportService.exportJasperReport(name, requestBody, response);
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request structure for report: " + name);
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No configuration available for report: " + name);
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating report: " + e.getMessage());
        }
    }

    // Method to validate the structure of the request body
    private boolean validateRequestStructure(Map<String, Object> requestBody, Map<String, Object> expectedStructure) {
        // Check if all expected keys are present in the request body
        for (String key : expectedStructure.keySet()) {
            if (!requestBody.containsKey(key)) {
                return false;
            }
        }
        return true;
    }
}