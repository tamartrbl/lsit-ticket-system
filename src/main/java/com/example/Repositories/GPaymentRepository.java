package com.example.Repositories;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.example.Models.Payment;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Repository
public class GPaymentRepository implements IPaymentRepository {
    private final String bucketName = "lsit-ticketing-bucket"; // Replace with your GCS bucket name
    private final String paymentFolder = "payments/";  // Folder to store customer data
    private final Storage storage;
    private final ObjectMapper objectMapper;

    public GPaymentRepository() {
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
    public void add(Payment payment) {
        payment.id = UUID.randomUUID();
        String fileName = paymentFolder + payment.id + ".json";
        saveToStorage(fileName, payment);
    }

    @Override
    public Payment get(UUID id) {
        String fileName = paymentFolder + id + ".json";
        return fetchFromStorage(fileName);
    }

        @Override
    public Payment getByCustomerAndEvent(UUID customerId, UUID eventId) {
        return list().values().stream() // Stream over the values (Payment objects)
                .filter(payment -> payment.customer.equals(customerId) && payment.event.equals(eventId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void update(Payment payment) {
        String fileName = paymentFolder + payment.id + ".json";
        if (storage.get(bucketName, fileName) != null) {
            saveToStorage(fileName, payment);
        }
    }

    @Override
    public boolean processRefund(UUID paymentId) {
        Payment payment = get(paymentId);
        if (payment == null || payment.state != Payment.PaymentState.COMPLETED) {
            return false;
        }
        payment.state = Payment.PaymentState.CANCELLED;
        update(payment);
        return true;
    }

    @Override
    public HashMap<UUID, Payment> list() {
        HashMap<UUID, Payment> payments = new HashMap<>();
        Bucket bucket = storage.get(bucketName);

        for (Blob blob : bucket.list(Storage.BlobListOption.prefix(paymentFolder)).iterateAll()) {
            if (blob.getName().endsWith(".json")) {
                Payment payment = fetchFromStorage(blob.getName());
                if (payment != null) {
                    payments.put(payment.id, payment);
                }
            }
        }

        return payments;
    }

    private void saveToStorage(String fileName, Payment payment) {
        try {
            byte[] data = objectMapper.writeValueAsBytes(payment);
            storage.create(Blob.newBuilder(bucketName, fileName).build(), data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save payment to storage", e);
        }
    }

    private Payment fetchFromStorage(String fileName) {
        try {
            Blob blob = storage.get(bucketName, fileName);
            if (blob == null) {
                return null;
            }
            byte[] data = blob.getContent();
            return objectMapper.readValue(data, Payment.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch payment from storage", e);
        }
    }
}
