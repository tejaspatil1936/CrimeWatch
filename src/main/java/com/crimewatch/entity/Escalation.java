package com.crimewatch.entity;

import com.crimewatch.enums.EscalationLevel;
import jakarta.validation.constraints.*;

import java.util.Objects;

public class Escalation {

    private String escalationId;

    @NotBlank
    private String zoneId;

    private long triggeredAt;

    @Min(1)
    private int reportCount;

    @NotNull
    private EscalationLevel escalationLevel;

    private boolean resolved = false;

    public Escalation() {}

    public Escalation(String escalationId, String zoneId, long triggeredAt,
                      int reportCount, EscalationLevel escalationLevel, boolean resolved) {
        this.escalationId = escalationId;
        this.zoneId = zoneId;
        this.triggeredAt = triggeredAt;
        this.reportCount = reportCount;
        this.escalationLevel = escalationLevel;
        this.resolved = resolved;
    }

    public String getEscalationId() { return escalationId; }
    public void setEscalationId(String escalationId) { this.escalationId = escalationId; }

    public String getZoneId() { return zoneId; }
    public void setZoneId(String zoneId) { this.zoneId = zoneId; }

    public long getTriggeredAt() { return triggeredAt; }
    public void setTriggeredAt(long triggeredAt) { this.triggeredAt = triggeredAt; }

    public int getReportCount() { return reportCount; }
    public void setReportCount(int reportCount) { this.reportCount = reportCount; }

    public EscalationLevel getEscalationLevel() { return escalationLevel; }
    public void setEscalationLevel(EscalationLevel escalationLevel) { this.escalationLevel = escalationLevel; }

    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }

    @Override
    public String toString() {
        return "Escalation{escalationId='" + escalationId + "', zoneId='" + zoneId + "', level=" + escalationLevel + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Escalation that = (Escalation) o;
        return Objects.equals(escalationId, that.escalationId);
    }

    @Override
    public int hashCode() { return Objects.hash(escalationId); }
}
