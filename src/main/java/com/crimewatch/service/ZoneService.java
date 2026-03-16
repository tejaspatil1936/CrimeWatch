package com.crimewatch.service;

import com.crimewatch.entity.Zone;
import com.crimewatch.repository.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ZoneService {

    @Autowired private ZoneRepository zoneRepo;

    public Zone save(Zone zone) {
        return zoneRepo.save(zone);
    }

    public Optional<Zone> findById(String zoneId) {
        return zoneRepo.findById(zoneId);
    }

    public List<Zone> findAll() {
        return zoneRepo.findAll();
    }

    public List<Zone> findByCity(String city) {
        return zoneRepo.findByCity(city);
    }

    public void deleteById(String zoneId) {
        zoneRepo.deleteById(zoneId);
    }

    public void updateThreshold(String zoneId, int threshold) {
        Zone zone = zoneRepo.findById(zoneId)
                .orElseThrow(() -> new IllegalArgumentException("Zone not found: " + zoneId));
        zone.setEscalationThreshold(threshold);
        zoneRepo.save(zone);
    }
}
