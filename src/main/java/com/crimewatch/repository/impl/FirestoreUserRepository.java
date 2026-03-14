package com.crimewatch.repository.impl;

import com.crimewatch.entity.User;
import com.crimewatch.enums.Role;
import com.crimewatch.repository.UserRepository;
import com.crimewatch.util.IdGenerator;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class FirestoreUserRepository implements UserRepository {

    private static final String COLLECTION = "users";

    private CollectionReference col() {
        return FirestoreClient.getFirestore().collection(COLLECTION);
    }

    @Override
    public User save(User user) {
        if (user.getUserId() == null) {
            user.setUserId(IdGenerator.userId());
        }
        if (user.getCreatedAt() == 0) {
            user.setCreatedAt(System.currentTimeMillis());
        }
        try {
            col().document(user.getUserId()).set(user).get();
            return user;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Firestore save user failed", e);
        }
    }

    @Override
    public Optional<User> findById(String userId) {
        try {
            DocumentSnapshot snap = col().document(userId).get().get();
            return snap.exists() ? Optional.ofNullable(snap.toObject(User.class)) : Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findById user failed", e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            List<QueryDocumentSnapshot> docs = col().whereEqualTo("email", email).get().get().getDocuments();
            return docs.isEmpty() ? Optional.empty() : Optional.ofNullable(docs.get(0).toObject(User.class));
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findByEmail failed", e);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try {
            List<QueryDocumentSnapshot> docs = col().whereEqualTo("username", username).get().get().getDocuments();
            return docs.isEmpty() ? Optional.empty() : Optional.ofNullable(docs.get(0).toObject(User.class));
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findByUsername failed", e);
        }
    }

    @Override
    public List<User> findByRole(Role role) {
        try {
            return col().whereEqualTo("role", role.name()).get().get()
                    .getDocuments().stream()
                    .map(d -> d.toObject(User.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findByRole failed", e);
        }
    }

    @Override
    public List<User> findAll() {
        try {
            return col().get().get().getDocuments().stream()
                    .map(d -> d.toObject(User.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("findAll users failed", e);
        }
    }

    @Override
    public void deleteById(String userId) {
        try {
            col().document(userId).delete().get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("deleteById user failed", e);
        }
    }

    @Override
    public long count() {
        try {
            return col().count().get().get().getCount();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("count users failed", e);
        }
    }
}
