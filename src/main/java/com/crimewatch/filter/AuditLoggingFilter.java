package com.crimewatch.filter;

import com.crimewatch.entity.AuditLog;
import com.crimewatch.repository.AuditLogRepository;
import com.crimewatch.util.IdGenerator;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class AuditLoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(AuditLoggingFilter.class);
    private static final Set<String> AUDIT_METHODS = Set.of("POST", "PUT", "DELETE", "PATCH");

    @Autowired private AuditLogRepository auditRepo;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) req;
        long start = System.currentTimeMillis();
        try {
            chain.doFilter(req, res);
        } finally {
            if (AUDIT_METHODS.contains(httpReq.getMethod())) {
                recordAudit(httpReq);
            }
            log.debug("{} {} took {}ms",
                      httpReq.getMethod(), httpReq.getRequestURI(),
                      System.currentTimeMillis() - start);
        }
    }

    private void recordAudit(HttpServletRequest req) {
        try {
            var authObj = SecurityContextHolder.getContext().getAuthentication();
            String userId = (authObj != null && authObj.isAuthenticated()) ? authObj.getName() : null;

            AuditLog entry = new AuditLog();
            entry.setLogId(IdGenerator.auditId());
            entry.setUserId(userId);
            entry.setAction(req.getMethod() + " " + req.getRequestURI());
            entry.setEntityType(extractEntityType(req.getRequestURI()));
            entry.setEntityId(req.getParameter("id"));
            entry.setTimestamp(System.currentTimeMillis());
            entry.setIpAddress(req.getRemoteAddr());
            auditRepo.save(entry);
        } catch (Exception e) {
            log.error("Audit log write failed", e);
        }
    }

    private String extractEntityType(String uri) {
        if (uri.contains("/report")) return "CrimeReport";
        if (uri.contains("/zone")) return "Zone";
        if (uri.contains("/user")) return "User";
        if (uri.contains("/assign")) return "Assignment";
        return "Other";
    }
}
