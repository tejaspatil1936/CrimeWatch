package com.crimewatch.dto;

import com.crimewatch.enums.CrimeType;
import com.crimewatch.enums.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReportSubmissionDto {

    @NotBlank @Size(min = 5, max = 200)
    private String title;

    @Size(max = 2000)
    private String description;

    @NotNull
    private CrimeType crimeType;

    @NotNull
    private Severity severity;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

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
}
