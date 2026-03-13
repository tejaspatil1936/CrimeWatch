package com.crimewatch.repository;

import com.crimewatch.entity.CrimeReport;
import com.crimewatch.enums.ReportStatus;

import java.util.List;
import java.util.Optional;

public interface CrimeReportRepository {

    CrimeReport save(CrimeReport report);

    Optional<CrimeReport> findById(String reportId);

    List<CrimeReport> findAll();

    List<CrimeReport> findByZoneId(String zoneId);

    List<CrimeReport> findByStatus(ReportStatus status);

    List<CrimeReport> findByReporterId(String reporterId);

    long countByZoneIdAndStatusIn(String zoneId, List<ReportStatus> statuses, long sinceTimestamp);

    void update(CrimeReport report);

    void deleteById(String reportId);

    long count();

    List<CrimeReport> findAllPaged(int pageSize, String lastDocId);
}
