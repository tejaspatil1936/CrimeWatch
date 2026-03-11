package com.crimewatch.entity;

import com.crimewatch.enums.CrimeType;
import com.crimewatch.enums.ReportStatus;
import com.crimewatch.enums.Severity;
import jakarta.validation.constraints.*;

import java.util.Date;
import java.util.Objects;

public class CrimeReport {

    private String reportId;

    @NotBlank @Size(min = 5, max = 200)
    private String title;

    @Size(max = 2000)
    private String description;

    @NotNull
    private CrimeType crimeType;

    @NotNull
    private Severity severity = Severity.MEDIUM;

    @NotNull @DecimalMin("-90") @DecimalMax("90")
    private Double latitude;

    @NotNull @DecimalMin("-180") @DecimalMax("180")
    private Double longitude;

    @NotBlank
    private String zoneId;

    private String reporterId;

    private ReportStatus status = ReportStatus.PENDING;

    private long reportedAt;

    private Long resolvedAt;

    public CrimeReport() {}

    public CrimeReport(String reportId, String title, String description, CrimeType crimeType,
                       Severity severity, Double latitude, Double longitude, String zoneId,
                       String reporterId, ReportStatus status, long reportedAt, Long resolvedAt) {
        this.reportId = reportId;
        this.title = title;
        this.description = description;
        this.crimeType = crimeType;
        this.severity = severity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.zoneId = zoneId;
        this.reporterId = reporterId;
        this.status = status;
        this.reportedAt = reportedAt;
        this.resolvedAt = resolvedAt;
    }

    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public CrimeType getCrimeType() { return crimeType; }
    public void setCrimeType(CrimeType crimeType) { this.crimeType = crimeType; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getZoneId() { return zoneId; }
    public void setZoneId(String zoneId) { this.zoneId = zoneId; }

    public String getReporterId() { return reporterId; }
    public void setReporterId(String reporterId) { this.reporterId = reporterId; }

    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }

    public long getReportedAt() { return reportedAt; }
    public void setReportedAt(long reportedAt) { this.reportedAt = reportedAt; }

    public Long getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Long resolvedAt) { this.resolvedAt = resolvedAt; }

    public Date getReportedAtDate() { return new Date(reportedAt); }

    @Override
    public String toString() {
        return "CrimeReport{reportId='" + reportId + "', title='" + title + "', status=" + status + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CrimeReport that = (CrimeReport) o;
        return Objects.equals(reportId, that.reportId);
    }

    @Override
    public int hashCode() { return Objects.hash(reportId); }
}
