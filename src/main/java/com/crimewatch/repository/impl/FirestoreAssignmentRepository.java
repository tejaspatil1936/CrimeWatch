package com.crimewatch.repository.impl;

import com.crimewatch.entity.Assignment;
import com.crimewatch.repository.AssignmentRepository;
import com.crimewatch.util.IdGenerator;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class FirestoreAssignmentRepository implements AssignmentRepository {

    private static final String COLLECTION = "assignments";

    private CollectionReference col() {
        return FirestoreClient.getFirestore().collection(COLLECTION);
    }

    @Override
    public Assignment save(Assignment assignment) {
        if (assignment.getAssignmentId() == null) {
            assignment.setAssignmentId(IdGenerator.assignmentId());
        }
        if (assignment.getAssignedAt() == 0) {
            assignment.setAssignedAt(System.currentTimeMillis());
        }
        try {
            col().document(assignment.getAssignmentId()).set(assignment).get();
            return assignment;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Firestore save assignment failed", e);
        }
    }

    @Override
    public Optional<Assignment> findById(String assignmentId) {
        try {
            DocumentSnapshot snap = col().document(assignmentId).get().get();
            return snap.exists() ? Optional.ofNullable(snap.toObject(Assignment.class)) : Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findById assignment failed", e);
        }
    }

    @Override
    public List<Assignment> findByReportId(String reportId) {
        try {
            return col().whereEqualTo("reportId", reportId).get().get()
                    .getDocuments().stream()
                    .map(d -> d.toObject(Assignment.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findByReportId failed", e);
        }
    }

    @Override
    public List<Assignment> findByOfficerId(String officerId) {
        try {
            return col().whereEqualTo("officerId", officerId).get().get()
                    .getDocuments().stream()
                    .map(d -> d.toObject(Assignment.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findByOfficerId failed", e);
        }
    }

    @Override
    public List<Assignment> findAll() {
        try {
            return col().get().get().getDocuments().stream()
                    .map(d -> d.toObject(Assignment.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findAll assignments failed", e);
        }
    }

    @Override
    public void deleteById(String assignmentId) {
        try {
            col().document(assignmentId).delete().get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("deleteById assignment failed", e);
        }
    }
}
