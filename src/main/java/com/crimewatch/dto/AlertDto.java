package com.crimewatch.dto;

import com.crimewatch.entity.CrimeReport;

public class AlertDto {

    private String reportId;
    private String title;
    private String crimeType;
    private String severity;
    private double latitude;
    private double longitude;
    private String zoneId;
    private String zoneName;
    private long timestamp;
    private String status;

    public static AlertDto from(CrimeReport report, String zoneName) {
        AlertDto dto = new AlertDto();
        dto.setReportId(report.getReportId());
        dto.setTitle(report.getTitle());
        dto.setCrimeType(report.getCrimeType().name());
        dto.setSeverity(report.getSeverity().name());
        dto.setLatitude(report.getLatitude());
        dto.setLongitude(report.getLongitude());
        dto.setZoneId(report.getZoneId());
        dto.setZoneName(zoneName);
        dto.setTimestamp(report.getReportedAt());
        dto.setStatus(report.getStatus().name());
        return dto;
    }

    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCrimeType() { return crimeType; }
    public void setCrimeType(String crimeType) { this.crimeType = crimeType; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getZoneId() { return zoneId; }
    public void setZoneId(String zoneId) { this.zoneId = zoneId; }

    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
