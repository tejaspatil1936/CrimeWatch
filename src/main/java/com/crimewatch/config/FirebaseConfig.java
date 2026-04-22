package com.crimewatch.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.credentials.path}")
    private String credentialsPath;

    @Value("${firebase.database.url}")
    private String databaseUrl;

    @Value("${firebase.project.id}")
    private String projectId;

    @PostConstruct
    public void init() throws Exception {
        if (!FirebaseApp.getApps().isEmpty()) {
            log.info("FirebaseApp already initialised");
            return;
        }

        // Support FIREBASE_CREDENTIALS_JSON env var for cloud deployments
        // Falls back to file path for local development
        InputStream credentialsStream;
        String envJson = System.getenv("FIREBASE_CREDENTIALS_JSON");
        if (envJson != null && !envJson.isBlank()) {
            log.info("Loading Firebase credentials from environment variable");
            credentialsStream = new ByteArrayInputStream(envJson.getBytes(StandardCharsets.UTF_8));
        } else {
            log.info("Loading Firebase credentials from file: {}", credentialsPath);
            credentialsStream = new FileInputStream(credentialsPath);
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                .setDatabaseUrl(databaseUrl)
                .setProjectId(projectId)
                .build();
        FirebaseApp.initializeApp(options);
        log.info("FirebaseApp initialised for project {}", projectId);
    }
}
