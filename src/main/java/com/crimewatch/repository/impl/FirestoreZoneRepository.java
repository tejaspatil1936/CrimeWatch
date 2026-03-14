package com.crimewatch.repository.impl;

import com.crimewatch.entity.Zone;
import com.crimewatch.repository.ZoneRepository;
import com.crimewatch.util.IdGenerator;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class FirestoreZoneRepository implements ZoneRepository {

    private static final String COLLECTION = "zones";

    private CollectionReference col() {
        return FirestoreClient.getFirestore().collection(COLLECTION);
    }

    @Override
    public Zone save(Zone zone) {
        if (zone.getZoneId() == null) {
            zone.setZoneId(IdGenerator.zoneId());
        }
        if (zone.getCreatedAt() == 0) {
            zone.setCreatedAt(System.currentTimeMillis());
        }
        try {
            col().document(zone.getZoneId()).set(zone).get();
            return zone;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Firestore save zone failed", e);
        }
    }

    @Override
    public Optional<Zone> findById(String zoneId) {
        try {
            DocumentSnapshot snap = col().document(zoneId).get().get();
            return snap.exists() ? Optional.ofNullable(snap.toObject(Zone.class)) : Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findById zone failed", e);
        }
    }

    @Override
    public List<Zone> findAll() {
        try {
            return col().get().get().getDocuments().stream()
                    .map(d -> d.toObject(Zone.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findAll zones failed", e);
        }
    }

    @Override
    public List<Zone> findByCity(String city) {
        try {
            return col().whereEqualTo("city", city).get().get()
                    .getDocuments().stream()
                    .map(d -> d.toObject(Zone.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findByCity failed", e);
        }
    }

    @Override
    public void deleteById(String zoneId) {
        try {
            col().document(zoneId).delete().get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("deleteById zone failed", e);
        }
    }

    @Override
    public long count() {
        try {
            return col().count().get().get().getCount();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("count zones failed", e);
        }
    }
}
