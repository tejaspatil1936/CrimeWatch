package com.crimewatch.service;

import com.crimewatch.dto.AlertDto;
import com.crimewatch.dto.ReportSubmissionDto;
import com.crimewatch.entity.CrimeReport;
import com.crimewatch.entity.Zone;
import com.crimewatch.enums.ReportStatus;
import com.crimewatch.repository.CrimeReportRepository;
import com.crimewatch.repository.ZoneRepository;
import com.crimewatch.socket.AlertSocketBroadcaster;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CrimeReportService {

    private static final Logger log = LoggerFactory.getLogger(CrimeReportService.class);

    @Autowired private CrimeReportRepository reportRepo;
    @Autowired private ZoneRepository zoneRepo;
    @Autowired private FirebaseAlertService firebaseAlertService;
    @Autowired private AlertSocketBroadcaster socketBroadcaster;
    @Autowired private AuditService auditService;

    public CrimeReport submitReport(ReportSubmissionDto dto, String reporterId, String ipAddress) {
        Zone zone = resolveZoneByProximity(dto.getLatitude(), dto.getLongitude());

        CrimeReport report = new CrimeReport();
        report.setTitle(dto.getTitle());
        report.setDescription(dto.getDescription());
        report.setCrimeType(dto.getCrimeType());
        report.setSeverity(dto.getSeverity());
        report.setLatitude(dto.getLatitude());
        report.setLongitude(dto.getLongitude());
        report.setZoneId(zone.getZoneId());
        report.setReporterId(reporterId);
        report.setStatus(ReportStatus.PENDING);
        report.setReportedAt(System.currentTimeMillis());

        Firestore db = FirestoreClient.getFirestore();

        try {
            db.runTransaction(txn -> {
                txn.set(db.collection("crime_reports").document(generateId(report)), report);
                return null;
            }).get();
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Transactional report submission failed", e);
        }

        AlertDto alert = AlertDto.from(report, zone.getZoneName());
        firebaseAlertService.pushAlert(alert);
        socketBroadcaster.broadcastJson(alert);
        auditService.log(reporterId, "REPORT_SUBMITTED", "CrimeReport",
                         report.getReportId(), ipAddress);

        log.info("Report submitted: id={}, zone={}, severity={}",
                 report.getReportId(), zone.getZoneId(), report.getSeverity());
        return report;
    }

    public Optional<CrimeReport> findById(String reportId) {
        return reportRepo.findById(reportId);
    }

    public List<CrimeReport> findAll() {
        return reportRepo.findAll();
    }

    public List<CrimeReport> findPending() {
        return reportRepo.findByStatus(ReportStatus.PENDING);
    }

    public List<CrimeReport> findActive() {
        List<CrimeReport> assigned   = reportRepo.findByStatus(ReportStatus.ASSIGNED);
        List<CrimeReport> inProgress = reportRepo.findByStatus(ReportStatus.IN_PROGRESS);
        List<CrimeReport> combined   = new java.util.ArrayList<>();
        combined.addAll(assigned);
        combined.addAll(inProgress);
        combined.sort((a, b) -> Long.compare(b.getReportedAt(), a.getReportedAt()));
        return combined;
    }

    public List<CrimeReport> findByZone(String zoneId) {
        return reportRepo.findByZoneId(zoneId);
    }

    public List<CrimeReport> findByReporterId(String reporterId) {
        return reportRepo.findByReporterId(reporterId);
    }

    public void assignToOfficer(String reportId, String officerId) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            db.runTransaction(txn -> {
                CrimeReport report = reportRepo.findById(reportId)
                        .orElseThrow(() -> new IllegalArgumentException("Report not found: " + reportId));
                report.setStatus(ReportStatus.ASSIGNED);
                txn.set(db.collection("crime_reports").document(reportId), report);
                return null;
            }).get();
        } catch (Exception e) {
            throw new RuntimeException("Assignment transaction failed", e);
        }
    }

    public void updateStatus(String reportId, String status) {
        CrimeReport report = reportRepo.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found: " + reportId));
        report.setStatus(ReportStatus.valueOf(status));
        if (ReportStatus.valueOf(status) == ReportStatus.RESOLVED) {
            report.setResolvedAt(System.currentTimeMillis());
        }
        reportRepo.update(report);
    }

    public List<CrimeReport> findAllPaged(int pageSize, String lastDocId) {
        return reportRepo.findAllPaged(pageSize, lastDocId);
    }

    private Zone resolveZoneByProximity(double lat, double lng) {
        List<Zone> zones = zoneRepo.findAll();
        return zones.stream()
                .min((a, b) -> Double.compare(
                        haversine(lat, lng, a.getLatCenter(), a.getLngCenter()),
                        haversine(lat, lng, b.getLatCenter(), b.getLngCenter())))
                .orElseThrow(() -> new IllegalStateException("No zones configured"));
    }

    private String generateId(CrimeReport r) {
        if (r.getReportId() == null) {
            r.setReportId(com.crimewatch.util.IdGenerator.reportId());
        }
        return r.getReportId();
    }

    private double haversine(double lat1, double lng1, double lat2, double lng2) {
        double R = 6371e3;
        double p1 = Math.toRadians(lat1), p2 = Math.toRadians(lat2);
        double dp = Math.toRadians(lat2 - lat1);
        double dl = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dp / 2) * Math.sin(dp / 2)
                + Math.cos(p1) * Math.cos(p2) * Math.sin(dl / 2) * Math.sin(dl / 2);
        return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
