package com.crimewatch.repository;

import com.crimewatch.entity.AuditLog;

import java.util.List;

public interface AuditLogRepository {

    AuditLog save(AuditLog log);

    List<AuditLog> findByUserId(String userId);

    List<AuditLog> findAll();

    List<AuditLog> findAllPaged(int pageSize, String lastDocId);

    List<AuditLog> findByEntityTypeAndEntityId(String entityType, String entityId);
}
