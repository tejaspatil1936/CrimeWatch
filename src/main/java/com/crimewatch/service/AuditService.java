package com.crimewatch.service;

import com.crimewatch.entity.AuditLog;
import com.crimewatch.repository.AuditLogRepository;
import com.crimewatch.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditService {

    @Autowired private AuditLogRepository auditRepo;

    public void log(String userId, String action, String entityType, String entityId, String ipAddress) {
        AuditLog entry = new AuditLog();
        entry.setLogId(IdGenerator.auditId());
        entry.setUserId(userId);
        entry.setAction(action);
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry.setTimestamp(System.currentTimeMillis());
        entry.setIpAddress(ipAddress);
        auditRepo.save(entry);
    }

    public List<AuditLog> findAll() {
        return auditRepo.findAll();
    }

    public List<AuditLog> findPaged(int page, int pageSize) {
        String lastDocId = null;
        List<AuditLog> result = null;
        for (int i = 0; i < page; i++) {
            result = auditRepo.findAllPaged(pageSize, lastDocId);
            if (!result.isEmpty()) {
                lastDocId = result.get(result.size() - 1).getLogId();
            }
        }
        return result;
    }

    public List<AuditLog> findByUserId(String userId) {
        return auditRepo.findByUserId(userId);
    }
}
