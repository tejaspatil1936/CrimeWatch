package com.crimewatch.repository;

import com.crimewatch.entity.Assignment;

import java.util.List;
import java.util.Optional;

public interface AssignmentRepository {

    Assignment save(Assignment assignment);

    Optional<Assignment> findById(String assignmentId);

    List<Assignment> findByReportId(String reportId);

    List<Assignment> findByOfficerId(String officerId);

    List<Assignment> findAll();

    void deleteById(String assignmentId);
}
