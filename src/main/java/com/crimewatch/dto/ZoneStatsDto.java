package com.crimewatch.dto;

public class ZoneStatsDto {

    private String zoneId;
    private String zoneName;
    private long totalReports;
    private long pending;
    private long resolved;
    private double avgResponseMinutes;

    public String getZoneId() { return zoneId; }
    public void setZoneId(String zoneId) { this.zoneId = zoneId; }

    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }

    public long getTotalReports() { return totalReports; }
    public void setTotalReports(long totalReports) { this.totalReports = totalReports; }

    public long getPending() { return pending; }
    public void setPending(long pending) { this.pending = pending; }

    public long getResolved() { return resolved; }
    public void setResolved(long resolved) { this.resolved = resolved; }

    public double getAvgResponseMinutes() { return avgResponseMinutes; }
    public void setAvgResponseMinutes(double avgResponseMinutes) { this.avgResponseMinutes = avgResponseMinutes; }
}
