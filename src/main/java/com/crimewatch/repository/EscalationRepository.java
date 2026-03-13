package com.crimewatch.repository;

import com.crimewatch.entity.Escalation;

import java.util.List;
import java.util.Optional;

public interface EscalationRepository {

    Escalation save(Escalation escalation);

    Optional<Escalation> findById(String escalationId);

    List<Escalation> findByZoneId(String zoneId);

    List<Escalation> findByResolvedFalse();

    List<Escalation> findAll();

    void update(Escalation escalation);
}
