package com.crimewatch.repository.impl;

import com.crimewatch.entity.CrimeReport;
import com.crimewatch.enums.ReportStatus;
import com.crimewatch.repository.CrimeReportRepository;
import com.crimewatch.util.IdGenerator;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class FirestoreCrimeReportRepository implements CrimeReportRepository {

    private static final String COLLECTION = "crime_reports";

    private CollectionReference col() {
        return FirestoreClient.getFirestore().collection(COLLECTION);
    }

    @Override
    public CrimeReport save(CrimeReport report) {
        if (report.getReportId() == null) {
            report.setReportId(IdGenerator.reportId());
        }
        if (report.getReportedAt() == 0) {
            report.setReportedAt(System.currentTimeMillis());
        }
        try {
            col().document(report.getReportId()).set(report).get();
            return report;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Firestore save failed", e);
        }
    }

    @Override
    public Optional<CrimeReport> findById(String reportId) {
        try {
            DocumentSnapshot snap = col().document(reportId).get().get();
            return snap.exists()
                    ? Optional.ofNullable(snap.toObject(CrimeReport.class))
                    : Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Firestore findById failed", e);
        }
    }

    @Override
    public List<CrimeReport> findAll() {
        try {
            return col().get().get().getDocuments().stream()
                    .map(d -> d.toObject(CrimeReport.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Firestore findAll failed", e);
        }
    }

    @Override
    public List<CrimeReport> findByZoneId(String zoneId) {
        try {
            return col().whereEqualTo("zoneId", zoneId).get().get()
                    .getDocuments().stream()
                    .map(d -> d.toObject(CrimeReport.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findByZoneId failed", e);
        }
    }

    @Override
    public List<CrimeReport> findByStatus(ReportStatus status) {
        try {
            return col().whereEqualTo("status", status.name()).get().get()
                    .getDocuments().stream()
                    .map(d -> d.toObject(CrimeReport.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findByStatus failed", e);
        }
    }

    @Override
    public List<CrimeReport> findByReporterId(String reporterId) {
        try {
            return col().whereEqualTo("reporterId", reporterId).get().get()
                    .getDocuments().stream()
                    .map(d -> d.toObject(CrimeReport.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findByReporterId failed", e);
        }
    }

    @Override
    public long countByZoneIdAndStatusIn(String zoneId, List<ReportStatus> statuses, long sinceTimestamp) {
        try {
            List<String> statusNames = statuses.stream().map(Enum::name).toList();
            return col()
                    .whereEqualTo("zoneId", zoneId)
                    .whereIn("status", statusNames)
                    .whereGreaterThanOrEqualTo("reportedAt", sinceTimestamp)
                    .get().get()
                    .size();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("countByZoneIdAndStatusIn failed", e);
        }
    }

    @Override
    public void update(CrimeReport report) {
        save(report);
    }

    @Override
    public void deleteById(String reportId) {
        try {
            col().document(reportId).delete().get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("deleteById failed", e);
        }
    }

    @Override
    public long count() {
        try {
            return col().count().get().get().getCount();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("count failed", e);
        }
    }

    @Override
    public List<CrimeReport> findAllPaged(int pageSize, String lastDocId) {
        try {
            Query q = col().orderBy("reportedAt").limit(pageSize);
            if (lastDocId != null) {
                DocumentSnapshot cursor = col().document(lastDocId).get().get();
                q = q.startAfter(cursor);
            }
            return q.get().get().getDocuments().stream()
                    .map(d -> d.toObject(CrimeReport.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findAllPaged failed", e);
        }
    }
}
