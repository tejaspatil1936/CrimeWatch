package com.crimewatch.entity;

import java.util.Date;
import java.util.Objects;

public class AuditLog {

    private String logId;
    private String userId;
    private String action;
    private String entityType;
    private String entityId;
    private long timestamp;
    private String ipAddress;

    public AuditLog() {}

    public AuditLog(String logId, String userId, String action, String entityType,
                    String entityId, long timestamp, String ipAddress) {
        this.logId = logId;
        this.userId = userId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.timestamp = timestamp;
        this.ipAddress = ipAddress;
    }

    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public Date getTimestampDate() { return new Date(timestamp); }

    @Override
    public String toString() {
        return "AuditLog{logId='" + logId + "', action='" + action + "', userId='" + userId + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditLog auditLog = (AuditLog) o;
        return Objects.equals(logId, auditLog.logId);
    }

    @Override
    public int hashCode() { return Objects.hash(logId); }
}
