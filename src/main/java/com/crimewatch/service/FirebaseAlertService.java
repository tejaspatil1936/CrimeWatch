package com.crimewatch.service;

import com.crimewatch.dto.AlertDto;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FirebaseAlertService {

    public void pushAlert(AlertDto alert) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("alerts")
                .push();

        Map<String, Object> payload = new HashMap<>();
        payload.put("reportId", alert.getReportId());
        payload.put("title", alert.getTitle());
        payload.put("crimeType", alert.getCrimeType());
        payload.put("severity", alert.getSeverity());
        payload.put("latitude", alert.getLatitude());
        payload.put("longitude", alert.getLongitude());
        payload.put("zoneId", alert.getZoneId());
        payload.put("zoneName", alert.getZoneName());
        payload.put("timestamp", alert.getTimestamp());
        payload.put("status", alert.getStatus());

        ref.setValueAsync(payload);
    }

    public void pushEscalation(String zoneId, String level, int reportCount) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("escalations")
                .child(zoneId);

        Map<String, Object> payload = new HashMap<>();
        payload.put("level", level);
        payload.put("reportCount", reportCount);
        payload.put("triggeredAt", System.currentTimeMillis());

        ref.setValueAsync(payload);
    }
}
