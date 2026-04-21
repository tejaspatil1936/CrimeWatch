package com.crimewatch.controller;

import com.crimewatch.service.CrimeReportService;
import com.crimewatch.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard")
@PreAuthorize("hasRole('OFFICER')")
public class DashboardController {

    @Autowired private CrimeReportService reportService;
    @Autowired private StatsService statsService;

    @Value("${firebase.database.url}")
    private String firebaseDatabaseUrl;

    @Value("${firebase.project.id}")
    private String firebaseProjectId;

    @GetMapping
    public String home(Authentication auth, Model model) {
        model.addAttribute("pending", reportService.findPending());
        model.addAttribute("active", reportService.findActive());
        model.addAttribute("officerId", auth.getName());
        model.addAttribute("firebaseDatabaseUrl", firebaseDatabaseUrl);
        model.addAttribute("firebaseProjectId", firebaseProjectId);
        return "police/dashboard";
    }

    @PostMapping("/reports/{id}/assign")
    public String assign(@PathVariable String id, Authentication auth) {
        reportService.assignToOfficer(id, auth.getName());
        return "redirect:/dashboard";
    }

    @PostMapping("/reports/{id}/status")
    public String updateStatus(@PathVariable String id, @RequestParam String status) {
        reportService.updateStatus(id, status);
        return "redirect:/dashboard";
    }

    @GetMapping("/zone/{zoneId}/stats")
    public String zoneStats(@PathVariable String zoneId, Model model) {
        model.addAttribute("stats", statsService.computeZoneStats(zoneId));
        return "police/zone-stats";
    }
}
