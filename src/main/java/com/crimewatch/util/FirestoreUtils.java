package com.crimewatch.util;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;

import java.util.List;
import java.util.stream.Collectors;

public final class FirestoreUtils {
    private FirestoreUtils() {}

    public static <T> List<T> toList(List<QueryDocumentSnapshot> docs, Class<T> clazz) {
        return docs.stream()
                .map(d -> d.toObject(clazz))
                .collect(Collectors.toList());
    }

    public static <T> T toObject(DocumentSnapshot snap, Class<T> clazz) {
        return snap.exists() ? snap.toObject(clazz) : null;
    }
}
