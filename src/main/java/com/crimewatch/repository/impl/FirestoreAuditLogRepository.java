package com.crimewatch.repository.impl;

import com.crimewatch.entity.AuditLog;
import com.crimewatch.repository.AuditLogRepository;
import com.crimewatch.util.IdGenerator;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class FirestoreAuditLogRepository implements AuditLogRepository {

    private static final String COLLECTION = "audit_log";

    private CollectionReference col() {
        return FirestoreClient.getFirestore().collection(COLLECTION);
    }

    @Override
    public AuditLog save(AuditLog log) {
        if (log.getLogId() == null) {
            log.setLogId(IdGenerator.auditId());
        }
        if (log.getTimestamp() == 0) {
            log.setTimestamp(System.currentTimeMillis());
        }
        try {
            col().document(log.getLogId()).set(log).get();
            return log;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Firestore save audit log failed", e);
        }
    }

    @Override
    public List<AuditLog> findByUserId(String userId) {
        try {
            return col().whereEqualTo("userId", userId).get().get()
                    .getDocuments().stream()
                    .map(d -> d.toObject(AuditLog.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findByUserId audit failed", e);
        }
    }

    @Override
    public List<AuditLog> findAll() {
        try {
            return col().orderBy("timestamp", Query.Direction.DESCENDING).get().get()
                    .getDocuments().stream()
                    .map(d -> d.toObject(AuditLog.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findAll audit failed", e);
        }
    }

    @Override
    public List<AuditLog> findAllPaged(int pageSize, String lastDocId) {
        try {
            Query q = col().orderBy("timestamp", Query.Direction.DESCENDING).limit(pageSize);
            if (lastDocId != null) {
                DocumentSnapshot cursor = col().document(lastDocId).get().get();
                q = q.startAfter(cursor);
            }
            return q.get().get().getDocuments().stream()
                    .map(d -> d.toObject(AuditLog.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findAllPaged audit failed", e);
        }
    }

    @Override
    public List<AuditLog> findByEntityTypeAndEntityId(String entityType, String entityId) {
        try {
            return col().whereEqualTo("entityType", entityType)
                    .whereEqualTo("entityId", entityId)
                    .get().get()
                    .getDocuments().stream()
                    .map(d -> d.toObject(AuditLog.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findByEntityTypeAndEntityId failed", e);
        }
    }
}
