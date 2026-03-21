package com.crimewatch.controller.api;

import com.crimewatch.entity.CrimeReport;
import com.crimewatch.service.CrimeReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
public class ReportApiController {

    @Autowired private CrimeReportService reportService;

    @GetMapping("/reports/points")
    public List<Map<String, Double>> getReportPoints() {
        return reportService.findAll().stream()
                .map(r -> Map.of("lat", r.getLatitude(), "lng", r.getLongitude()))
                .collect(Collectors.toList());
    }

    @GetMapping("/reports")
    public List<CrimeReport> getAllReports() {
        return reportService.findAll();
    }
}
