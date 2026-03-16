package com.crimewatch.service;

import com.crimewatch.entity.Escalation;
import com.crimewatch.entity.Zone;
import com.crimewatch.enums.EscalationLevel;
import com.crimewatch.enums.ReportStatus;
import com.crimewatch.repository.CrimeReportRepository;
import com.crimewatch.repository.EscalationRepository;
import com.crimewatch.repository.ZoneRepository;
import com.crimewatch.socket.AlertSocketBroadcaster;
import com.crimewatch.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EscalationService {

    private static final Logger log = LoggerFactory.getLogger(EscalationService.class);

    @Autowired private ZoneRepository zoneRepo;
    @Autowired private CrimeReportRepository reportRepo;
    @Autowired private EscalationRepository escRepo;
    @Autowired private FirebaseAlertService firebaseAlertService;
    @Autowired private AlertSocketBroadcaster socketBroadcaster;

    @Value("${crimewatch.escalation.lookback-minutes}")
    private int lookbackMinutes;

    @Async("crimewatchTaskExecutor")
    public void evaluateZone(Zone zone) {
        long since = System.currentTimeMillis() - (lookbackMinutes * 60_000L);
        long count = reportRepo.countByZoneIdAndStatusIn(
                zone.getZoneId(),
                List.of(ReportStatus.PENDING, ReportStatus.ASSIGNED),
                since);

        if (count >= zone.getEscalationThreshold()) {
            EscalationLevel level = count >= zone.getEscalationThreshold() * 2
                    ? EscalationLevel.CRITICAL
                    : EscalationLevel.ALERT;

            Escalation esc = new Escalation();
            esc.setEscalationId(IdGenerator.escalationId());
            esc.setZoneId(zone.getZoneId());
            esc.setTriggeredAt(System.currentTimeMillis());
            esc.setReportCount((int) count);
            esc.setEscalationLevel(level);
            esc.setResolved(false);
            escRepo.save(esc);

            firebaseAlertService.pushEscalation(zone.getZoneId(), level.name(), (int) count);
            socketBroadcaster.broadcastJson(esc);

            log.warn("ESCALATION TRIGGERED zone={} level={} count={}",
                     zone.getZoneId(), level, count);
        }
    }
}
