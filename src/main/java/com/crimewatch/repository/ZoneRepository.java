package com.crimewatch.repository;

import com.crimewatch.entity.Zone;

import java.util.List;
import java.util.Optional;

public interface ZoneRepository {

    Zone save(Zone zone);

    Optional<Zone> findById(String zoneId);

    List<Zone> findAll();

    List<Zone> findByCity(String city);

    void deleteById(String zoneId);

    long count();
}
