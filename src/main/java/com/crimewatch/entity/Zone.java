package com.crimewatch.entity;

import jakarta.validation.constraints.*;

import java.util.Objects;

public class Zone {

    private String zoneId;

    @NotBlank @Size(max = 100)
    private String zoneName;

    @NotBlank @Size(max = 100)
    private String city;

    @NotNull
    private Double latCenter;

    @NotNull
    private Double lngCenter;

    @Min(1) @Max(100)
    private int escalationThreshold = 5;

    private long createdAt;

    public Zone() {}

    public Zone(String zoneId, String zoneName, String city, Double latCenter,
                Double lngCenter, int escalationThreshold, long createdAt) {
        this.zoneId = zoneId;
        this.zoneName = zoneName;
        this.city = city;
        this.latCenter = latCenter;
        this.lngCenter = lngCenter;
        this.escalationThreshold = escalationThreshold;
        this.createdAt = createdAt;
    }

    public String getZoneId() { return zoneId; }
    public void setZoneId(String zoneId) { this.zoneId = zoneId; }

    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Double getLatCenter() { return latCenter; }
    public void setLatCenter(Double latCenter) { this.latCenter = latCenter; }

    public Double getLngCenter() { return lngCenter; }
    public void setLngCenter(Double lngCenter) { this.lngCenter = lngCenter; }

    public int getEscalationThreshold() { return escalationThreshold; }
    public void setEscalationThreshold(int escalationThreshold) { this.escalationThreshold = escalationThreshold; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Zone{zoneId='" + zoneId + "', zoneName='" + zoneName + "', city='" + city + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Zone zone = (Zone) o;
        return Objects.equals(zoneId, zone.zoneId);
    }

    @Override
    public int hashCode() { return Objects.hash(zoneId); }
}
