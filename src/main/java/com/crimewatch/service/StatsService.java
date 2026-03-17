package com.crimewatch.service;

import com.crimewatch.dto.ZoneStatsDto;
import com.crimewatch.entity.CrimeReport;
import com.crimewatch.entity.Zone;
import com.crimewatch.enums.ReportStatus;
import com.crimewatch.repository.CrimeReportRepository;
import com.crimewatch.repository.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsService {

    @Autowired private CrimeReportRepository reportRepo;
    @Autowired private ZoneRepository zoneRepo;

    public ZoneStatsDto computeZoneStats(String zoneId) {
        Zone zone = zoneRepo.findById(zoneId)
                .orElseThrow(() -> new IllegalArgumentException("Zone not found: " + zoneId));
        List<CrimeReport> reports = reportRepo.findByZoneId(zoneId);

        ZoneStatsDto dto = new ZoneStatsDto();
        dto.setZoneId(zoneId);
        dto.setZoneName(zone.getZoneName());
        dto.setTotalReports(reports.size());
        dto.setPending(reports.stream().filter(r -> r.getStatus() == ReportStatus.PENDING).count());
        dto.setResolved(reports.stream().filter(r -> r.getStatus() == ReportStatus.RESOLVED).count());

        double avgMinutes = reports.stream()
                .filter(r -> r.getStatus() == ReportStatus.RESOLVED && r.getResolvedAt() != null)
                .mapToDouble(r -> (r.getResolvedAt() - r.getReportedAt()) / 60_000.0)
                .average()
                .orElse(0);
        dto.setAvgResponseMinutes(avgMinutes);

        return dto;
    }

    public List<Map<String, Object>> computeWeeklyAvgResponseTime() {
        List<CrimeReport> resolved = reportRepo.findAll().stream()
                .filter(r -> r.getStatus() == ReportStatus.RESOLVED && r.getResolvedAt() != null)
                .collect(Collectors.toList());

        Map<String, List<CrimeReport>> byWeek = resolved.stream()
                .collect(Collectors.groupingBy(r -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(r.getReportedAt());
                    return cal.get(Calendar.YEAR) + "-W" + String.format("%02d", cal.get(Calendar.WEEK_OF_YEAR));
                }));

        return byWeek.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    double avg = entry.getValue().stream()
                            .mapToDouble(r -> (r.getResolvedAt() - r.getReportedAt()) / 60_000.0)
                            .average().orElse(0);
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("week", entry.getKey());
                    m.put("avgMinutes", Math.round(avg * 100.0) / 100.0);
                    return m;
                })
                .collect(Collectors.toList());
    }

    public Map<String, Long> reportsByZone() {
        List<Zone> zones = zoneRepo.findAll();
        Map<String, Long> result = new LinkedHashMap<>();
        for (Zone z : zones) {
            long count = reportRepo.findByZoneId(z.getZoneId()).size();
            result.put(z.getZoneName(), count);
        }
        return result;
    }

    public Map<String, Long> crimeTypeDistribution() {
        return reportRepo.findAll().stream()
                .collect(Collectors.groupingBy(r -> r.getCrimeType().name(), Collectors.counting()));
    }
}
