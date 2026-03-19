package com.crimewatch.scheduler;

import com.crimewatch.entity.Zone;
import com.crimewatch.repository.ZoneRepository;
import com.crimewatch.service.EscalationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EscalationScheduler {

    private static final Logger log = LoggerFactory.getLogger(EscalationScheduler.class);

    @Autowired private ZoneRepository zoneRepo;
    @Autowired private EscalationService escalationService;

    @Scheduled(fixedDelayString = "${crimewatch.escalation.fixed-delay-ms}")
    public void sweepZones() {
        List<Zone> zones = zoneRepo.findAll();
        log.info("Escalation sweep starting — {} zones", zones.size());
        for (Zone z : zones) {
            escalationService.evaluateZone(z);
        }
    }
}
