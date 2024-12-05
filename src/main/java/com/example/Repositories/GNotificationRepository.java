package com.example.Repositories;

import com.example.Models.Customer;
import com.example.Models.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class GNotificationRepository {
    private final String bucketName = "lsit-ticketing-bucket"; // Replace with your GCS bucket name
    private final String notificationFolder = "notifications/"; // Folder to store notification data
    private final Storage storage;
    private final ObjectMapper objectMapper;

    public GNotificationRepository() {
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

    public void addNotification(Notification notification) {
        String fileName = notificationFolder + notification.id + ".json";
        saveToStorage(fileName, notification);
    }

    public Notification getNotification(UUID id) {
        String fileName = notificationFolder + id + ".json";
        return fetchFromStorage(fileName);
    }

    public List<Notification> listNotifications() {
        List<Notification> notifications = new ArrayList<>();
        Bucket bucket = storage.get(bucketName);

        for (Blob blob : bucket.list(Storage.BlobListOption.prefix(notificationFolder)).iterateAll()) {
            if (blob.getName().endsWith(".json")) {
                Notification notification = fetchFromStorage(blob.getName());
                if (notification != null) {
                    notifications.add(notification);
                }
            }
        }

        return notifications;
    }

    public List<Notification> getAllNotificationsFromCustomer(UUID customerId) {
        List<Notification> customerNotifications = new ArrayList<>();
        for (Notification notification : listNotifications()) {
            if (notification.customerId.equals(customerId)) {
                customerNotifications.add(notification);
            }
        }
        return customerNotifications;
    }

    public Notification getCustomerLastNotification(UUID customerId) {
        return listNotifications().stream()
                .filter(notification -> notification.customerId.equals(customerId))
                .max((n1, n2) -> n1.timestamp.compareTo(n2.timestamp))
                .orElse(null);
    }

    private void saveToStorage(String fileName, Notification notification) {
        try {
            byte[] data = objectMapper.writeValueAsBytes(notification);
            storage.create(Blob.newBuilder(bucketName, fileName).build(), data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save notification to storage", e);
        }
    }

    private Notification fetchFromStorage(String fileName) {
        try {
            Blob blob = storage.get(bucketName, fileName);
            if (blob == null) {
                return null;
            }
            byte[] data = blob.getContent();
            return objectMapper.readValue(data, Notification.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch notification from storage", e);
        }
    }

    public void notifyAllCustomers(String message) {
        // Assuming you have a GCustomerRepository to fetch customers
        GCustomerRepository customerRepository = new GCustomerRepository();
        List<Customer> customers = customerRepository.list();

        for (Customer customer : customers) {
            Notification notification = new Notification(customer.id, message);
            addNotification(notification); // Reusing the addNotification method
        }
    }
}
