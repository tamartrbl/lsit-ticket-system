package com.example.Repositories;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.example.Models.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Repository
public class GEventRepository implements IEventRepository {
    private final String bucketName = "lsit-ticketing-bucket"; // Replace with your GCS bucket name
    private final String eventFolder = "events/";  // Folder to store customer data
    private final Storage storage;
    private final ObjectMapper objectMapper;

    public GEventRepository() {
        this.storage = initializeStorage();
        this.objectMapper = new ObjectMapper();
    }

    private Storage initializeStorage() {
        try {
            String credentialsFilePath = "lsit-ticket-system/src/main/resources/credentials.txt";
            String credentialsJson = new String(Files.readAllBytes(Paths.get(credentialsFilePath)));

            // Parse the JSON string into GoogleCredentials
            GoogleCredentials credentials = ServiceAccountCredentials.fromStream(
                    new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8))
            );

            // Build and return the Google Cloud Storage service
            return StorageOptions.newBuilder()
                    .setCredentials(credentials)
                    .build()
                    .getService();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Google Cloud Storage", e);
        }
    }

    @Override
    public void add(Event event) {
        event.id = UUID.randomUUID();
        saveToStorage(event);
    }

    @Override
    public Event get(UUID id) {
        return fetchFromStorage(eventFolder + id + ".json");
    }

    @Override
    public List<Event> list() {
        List<Event> events = new ArrayList<>();
        Bucket bucket = storage.get(bucketName);

        for (Blob blob : bucket.list(Storage.BlobListOption.prefix(eventFolder)).iterateAll()) {
            if (blob.getName().endsWith(".json")) {
                Event event = fetchFromStorage(blob.getName());
                if (event != null) {
                    events.add(event);
                }
            }
        }

        return events;
    }

    @Override
    public void remove(UUID id) {
        storage.delete(bucketName, eventFolder + id + ".json");
    }

    @Override
    public void update(Event event) {
        Event existingEvent = get(event.id);
        if (existingEvent != null) {
            saveToStorage(event);
        }
    }

    private void saveToStorage(Event event) {
        try {
            byte[] data = objectMapper.writeValueAsBytes(event);
            storage.create(Blob.newBuilder(bucketName, eventFolder + event.id + ".json").build(), data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save event to storage", e);
        }
    }

    private Event fetchFromStorage(String fileName) {
        try {
            Blob blob = storage.get(bucketName, fileName);
            if (blob == null) return null;
            return objectMapper.readValue(blob.getContent(), Event.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch event from storage", e);
        }
    }
}
