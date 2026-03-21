package com.crimewatch.controller.api;

import com.crimewatch.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsApiController {

    @Autowired private StatsService statsService;

    @GetMapping("/weekly-response-time")
    public List<Map<String, Object>> weeklyResponseTime() {
        return statsService.computeWeeklyAvgResponseTime();
    }

    @GetMapping("/reports-by-zone")
    public Map<String, Long> reportsByZone() {
        return statsService.reportsByZone();
    }

    @GetMapping("/crime-type-distribution")
    public Map<String, Long> crimeTypeDistribution() {
        return statsService.crimeTypeDistribution();
    }
}
