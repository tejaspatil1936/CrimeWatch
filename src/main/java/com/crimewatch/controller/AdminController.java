package com.crimewatch.controller;

import com.crimewatch.entity.Zone;
import com.crimewatch.enums.Role;
import com.crimewatch.repository.EscalationRepository;
import com.crimewatch.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired private ZoneService zoneService;
    @Autowired private UserService userService;
    @Autowired private ExportService exportService;
    @Autowired private AuditService auditService;
    @Autowired private EscalationRepository escRepo;
    @Autowired private StatsService statsService;

    @GetMapping
    public String home(Model model) {
        model.addAttribute("weeklyResponseTime", statsService.computeWeeklyAvgResponseTime());
        model.addAttribute("reportsByZone", statsService.reportsByZone());
        model.addAttribute("crimeTypeDistribution", statsService.crimeTypeDistribution());
        return "admin/home";
    }

    @GetMapping("/zones")
    public String zones(Model model) {
        model.addAttribute("zones", zoneService.findAll());
        model.addAttribute("zone", new Zone());
        return "admin/zones";
    }

    @PostMapping("/zones/add")
    public String addZone(@Valid @ModelAttribute("zone") Zone zone, BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("zones", zoneService.findAll());
            return "admin/zones";
        }
        zoneService.save(zone);
        return "redirect:/admin/zones";
    }

    @PostMapping("/zones/{id}/delete")
    public String deleteZone(@PathVariable String id) {
        zoneService.deleteById(id);
        return "redirect:/admin/zones";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @PostMapping("/users/{id}/role")
    public String changeRole(@PathVariable String id, @RequestParam Role role) {
        userService.changeRole(id, role);
        return "redirect:/admin/users";
    }

    @GetMapping("/escalations")
    public String escalations(Model model) {
        model.addAttribute("escalations", escRepo.findAll());
        return "admin/escalations";
    }

    @GetMapping("/audit")
    public String audit(@RequestParam(defaultValue = "1") int page, Model model) {
        model.addAttribute("logs", auditService.findPaged(page, 50));
        return "admin/audit-log";
    }

    @GetMapping("/export")
    public ResponseEntity<Resource> export() {
        return exportService.exportAllReportsAsCsv();
    }
}
