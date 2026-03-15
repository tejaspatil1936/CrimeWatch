package com.crimewatch.repository.impl;

import com.crimewatch.entity.Escalation;
import com.crimewatch.repository.EscalationRepository;
import com.crimewatch.util.IdGenerator;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class FirestoreEscalationRepository implements EscalationRepository {

    private static final String COLLECTION = "escalations";

    private CollectionReference col() {
        return FirestoreClient.getFirestore().collection(COLLECTION);
    }

    @Override
    public Escalation save(Escalation escalation) {
        if (escalation.getEscalationId() == null) {
            escalation.setEscalationId(IdGenerator.escalationId());
        }
        if (escalation.getTriggeredAt() == 0) {
            escalation.setTriggeredAt(System.currentTimeMillis());
        }
        try {
            col().document(escalation.getEscalationId()).set(escalation).get();
            return escalation;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Firestore save escalation failed", e);
        }
    }

    @Override
    public Optional<Escalation> findById(String escalationId) {
        try {
            DocumentSnapshot snap = col().document(escalationId).get().get();
            return snap.exists() ? Optional.ofNullable(snap.toObject(Escalation.class)) : Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findById escalation failed", e);
        }
    }

    @Override
    public List<Escalation> findByZoneId(String zoneId) {
        try {
            return col().whereEqualTo("zoneId", zoneId).get().get()
                    .getDocuments().stream()
                    .map(d -> d.toObject(Escalation.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findByZoneId escalation failed", e);
        }
    }

    @Override
    public List<Escalation> findByResolvedFalse() {
        try {
            return col().whereEqualTo("resolved", false).get().get()
                    .getDocuments().stream()
                    .map(d -> d.toObject(Escalation.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findByResolvedFalse failed", e);
        }
    }

    @Override
    public List<Escalation> findAll() {
        try {
            return col().get().get().getDocuments().stream()
                    .map(d -> d.toObject(Escalation.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findAll escalations failed", e);
        }
    }

    @Override
    public void update(Escalation escalation) {
        save(escalation);
    }
}
