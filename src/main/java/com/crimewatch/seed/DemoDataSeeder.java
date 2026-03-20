package com.crimewatch.seed;

import com.crimewatch.entity.*;
import com.crimewatch.enums.*;
import com.crimewatch.repository.*;
import com.crimewatch.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DemoDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    @Autowired private ZoneRepository zoneRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private CrimeReportRepository reportRepo;
    @Autowired private PasswordEncoder encoder;

    @Override
    public void run(ApplicationArguments args) {
        if (zoneRepo.count() > 0) {
            log.info("Seed skipped — data already present.");
            return;
        }
        log.info("Seeding demo data...");

        Zone z1 = new Zone(null, "Central District",  "Pune", 18.5204, 73.8567, 5, System.currentTimeMillis());
        Zone z2 = new Zone(null, "Kothrud",           "Pune", 18.5074, 73.8077, 4, System.currentTimeMillis());
        Zone z3 = new Zone(null, "Hinjawadi IT Park", "Pune", 18.5912, 73.7389, 6, System.currentTimeMillis());
        List<Zone> zones = List.of(z1, z2, z3);
        zones.forEach(z -> { z.setZoneId(IdGenerator.zoneId()); zoneRepo.save(z); });

        userRepo.save(buildUser("admin",   "admin@crimewatch.local",   Role.ADMIN,   null,             "admin123"));
        userRepo.save(buildUser("officer1","officer1@crimewatch.local",Role.OFFICER, z1.getZoneId(),   "officer123"));
        userRepo.save(buildUser("officer2","officer2@crimewatch.local",Role.OFFICER, z2.getZoneId(),   "officer123"));
        userRepo.save(buildUser("citizen", "citizen@crimewatch.local", Role.CITIZEN, null,             "citizen123"));

        reportRepo.save(buildReport("Unattended bag at station", CrimeType.SUSPICIOUS, Severity.MEDIUM,
                                    18.5211, 73.8571, z1.getZoneId(), null));
        reportRepo.save(buildReport("Phone snatching incident",  CrimeType.THEFT,      Severity.HIGH,
                                    18.5072, 73.8080, z2.getZoneId(), null));
        reportRepo.save(buildReport("Vandalism at bus stop",     CrimeType.VANDALISM,  Severity.LOW,
                                    18.5910, 73.7391, z3.getZoneId(), null));
        reportRepo.save(buildReport("Loud altercation on street", CrimeType.ASSAULT,   Severity.CRITICAL,
                                    18.5205, 73.8570, z1.getZoneId(), null));

        log.info("Demo data seeded: {} zones, 4 users, 4 reports.", zones.size());
    }

    private User buildUser(String username, String email, Role role, String zoneId, String password) {
        User u = new User();
        u.setUserId(IdGenerator.userId());
        u.setUsername(username);
        u.setEmail(email);
        u.setPasswordHash(encoder.encode(password));
        u.setRole(role);
        u.setZoneId(zoneId);
        u.setEnabled(true);
        u.setCreatedAt(System.currentTimeMillis());
        return u;
    }

    private CrimeReport buildReport(String title, CrimeType type, Severity sev,
                                    double lat, double lng, String zoneId, String reporterId) {
        CrimeReport r = new CrimeReport();
        r.setReportId(IdGenerator.reportId());
        r.setTitle(title);
        r.setDescription(title + " — seeded demo entry.");
        r.setCrimeType(type);
        r.setSeverity(sev);
        r.setLatitude(lat);
        r.setLongitude(lng);
        r.setZoneId(zoneId);
        r.setReporterId(reporterId);
        r.setStatus(ReportStatus.PENDING);
        r.setReportedAt(System.currentTimeMillis());
        return r;
    }
}
