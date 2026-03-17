package com.crimewatch.service;

import com.crimewatch.entity.CrimeReport;
import com.crimewatch.repository.CrimeReportRepository;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.List;

@Service
public class ExportService {

    private static final int PAGE_SIZE = 500;

    @Autowired private CrimeReportRepository reportRepo;

    public ResponseEntity<Resource> exportAllReportsAsCsv() {
        StringWriter sw = new StringWriter();
        try (CSVWriter writer = new CSVWriter(sw)) {
            writer.writeNext(new String[]{
                "Report ID", "Title", "Crime Type", "Severity", "Status",
                "Latitude", "Longitude", "Zone ID", "Reporter ID",
                "Reported At", "Resolved At"
            });

            String lastDocId = null;
            while (true) {
                List<CrimeReport> page = reportRepo.findAllPaged(PAGE_SIZE, lastDocId);
                if (page.isEmpty()) break;

                for (CrimeReport r : page) {
                    writer.writeNext(new String[]{
                        r.getReportId(),
                        r.getTitle(),
                        r.getCrimeType() != null ? r.getCrimeType().name() : "",
                        r.getSeverity() != null ? r.getSeverity().name() : "",
                        r.getStatus() != null ? r.getStatus().name() : "",
                        String.valueOf(r.getLatitude()),
                        String.valueOf(r.getLongitude()),
                        r.getZoneId(),
                        r.getReporterId() != null ? r.getReporterId() : "anonymous",
                        String.valueOf(r.getReportedAt()),
                        r.getResolvedAt() != null ? String.valueOf(r.getResolvedAt()) : ""
                    });
                }

                lastDocId = page.get(page.size() - 1).getReportId();
                if (page.size() < PAGE_SIZE) break;
            }
        } catch (Exception e) {
            throw new RuntimeException("CSV export failed", e);
        }

        byte[] csvBytes = sw.toString().getBytes();
        ByteArrayResource resource = new ByteArrayResource(csvBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=crimewatch_reports.csv")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(csvBytes.length)
                .body(resource);
    }
}
