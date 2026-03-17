package com.crimewatch.service;

import com.crimewatch.entity.Assignment;
import com.crimewatch.entity.CrimeReport;
import com.crimewatch.enums.ReportStatus;
import com.crimewatch.repository.AssignmentRepository;
import com.crimewatch.repository.CrimeReportRepository;
import com.crimewatch.util.IdGenerator;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentService {

    @Autowired private AssignmentRepository assignmentRepo;
    @Autowired private CrimeReportRepository reportRepo;

    public Assignment assignReportToOfficer(String reportId, String officerId, String notes) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            return db.runTransaction(txn -> {
                CrimeReport report = reportRepo.findById(reportId)
                        .orElseThrow(() -> new IllegalArgumentException("Report not found: " + reportId));
                report.setStatus(ReportStatus.ASSIGNED);
                txn.set(db.collection("crime_reports").document(reportId), report);

                Assignment assignment = new Assignment();
                assignment.setAssignmentId(IdGenerator.assignmentId());
                assignment.setReportId(reportId);
                assignment.setOfficerId(officerId);
                assignment.setAssignedAt(System.currentTimeMillis());
                assignment.setNotes(notes);
                txn.set(db.collection("assignments").document(assignment.getAssignmentId()), assignment);

                return assignment;
            }).get();
        } catch (Exception e) {
            throw new RuntimeException("Assignment transaction failed", e);
        }
    }

    public List<Assignment> findByOfficer(String officerId) {
        return assignmentRepo.findByOfficerId(officerId);
    }

    public List<Assignment> findByReport(String reportId) {
        return assignmentRepo.findByReportId(reportId);
    }

    public List<Assignment> findAll() {
        return assignmentRepo.findAll();
    }
}
