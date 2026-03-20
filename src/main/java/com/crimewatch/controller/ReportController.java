package com.crimewatch.controller;

import com.crimewatch.dto.ReportSubmissionDto;
import com.crimewatch.entity.CrimeReport;
import com.crimewatch.service.CrimeReportService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/report")
public class ReportController {

    @Autowired private CrimeReportService service;

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("reportDto", new ReportSubmissionDto());
        return "citizen/report-form";
    }

    @PostMapping("/submit")
    public String submit(@Valid @ModelAttribute("reportDto") ReportSubmissionDto dto,
                         BindingResult br,
                         Authentication auth,
                         HttpServletRequest req,
                         Model model) {
        if (br.hasErrors()) {
            return "citizen/report-form";
        }
        String reporterId = (auth != null && auth.isAuthenticated()) ? auth.getName() : null;
        CrimeReport saved = service.submitReport(dto, reporterId, req.getRemoteAddr());
        model.addAttribute("report", saved);
        return "redirect:/report/success?id=" + saved.getReportId();
    }

    @GetMapping("/success")
    public String success(@RequestParam String id, Model model) {
        model.addAttribute("reportId", id);
        return "citizen/report-success";
    }

    @GetMapping("/my")
    public String myReports(Authentication auth, Model model) {
        List<CrimeReport> reports = service.findByReporterId(auth.getName());
        model.addAttribute("reports", reports);
        return "citizen/my-reports";
    }
}
