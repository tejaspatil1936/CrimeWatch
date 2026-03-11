package com.crimewatch.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class Assignment {

    private String assignmentId;

    @NotBlank
    private String reportId;

    @NotBlank
    private String officerId;

    private long assignedAt;

    @Size(max = 1000)
    private String notes;

    public Assignment() {}

    public Assignment(String assignmentId, String reportId, String officerId,
                      long assignedAt, String notes) {
        this.assignmentId = assignmentId;
        this.reportId = reportId;
        this.officerId = officerId;
        this.assignedAt = assignedAt;
        this.notes = notes;
    }

    public String getAssignmentId() { return assignmentId; }
    public void setAssignmentId(String assignmentId) { this.assignmentId = assignmentId; }

    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }

    public String getOfficerId() { return officerId; }
    public void setOfficerId(String officerId) { this.officerId = officerId; }

    public long getAssignedAt() { return assignedAt; }
    public void setAssignedAt(long assignedAt) { this.assignedAt = assignedAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "Assignment{assignmentId='" + assignmentId + "', reportId='" + reportId + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assignment that = (Assignment) o;
        return Objects.equals(assignmentId, that.assignmentId);
    }

    @Override
    public int hashCode() { return Objects.hash(assignmentId); }
}
