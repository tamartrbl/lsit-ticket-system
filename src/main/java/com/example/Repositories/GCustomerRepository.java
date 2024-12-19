package com.example.Repositories;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.example.Models.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Repository
public class GCustomerRepository implements ICustomerRepository {
    private final String bucketName = "lsit-ticketing-bucket"; // Replace with your GCS bucket name
    private final String customerFolder = "customers/";  // Folder to store customer data
    private final Storage storage;
    private final ObjectMapper objectMapper;

    public GCustomerRepository() {
        this.storage = initializeStorage();
        this.objectMapper = new ObjectMapper();
    }

    //AIzaSyDr0QCfnUpon3iDUTAe0X9WtuLGzl35_Hw

    private Storage initializeStorage() {
    try {
        // Use ClassLoader to load the file from the resources folder
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("credentials.txt");

        if (inputStream == null) {
            throw new RuntimeException("Failed to find credentials.txt in resources folder");
        }

        // Parse the InputStream into GoogleCredentials
        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(inputStream);

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
    public void add(Customer customer) {
        customer.id = UUID.randomUUID();
        String fileName = customerFolder + customer.id + ".json";
        saveToStorage(fileName, customer);
    }

    @Override
    public Customer get(UUID id) {
        String fileName = customerFolder + id + ".json";
        return fetchFromStorage(fileName);
    }

    @Override
    public void remove(UUID id) {
        String fileName = customerFolder + id + ".json";
        storage.delete(bucketName, fileName);
    }

    @Override
    public void update(Customer customer) {
        String fileName = customerFolder + customer.id + ".json";
        if (storage.get(bucketName, fileName) != null) {
            saveToStorage(fileName, customer);
        }
    }

    @Override
    public List<Customer> list() {
        List<Customer> customerList = new ArrayList<>();
        Bucket bucket = storage.get(bucketName);

        for (Blob blob : bucket.list(Storage.BlobListOption.prefix(customerFolder)).iterateAll()) {
            if (blob.getName().endsWith(".json")) {
                Customer customer = fetchFromStorage(blob.getName());
                if (customer != null) {
                    customerList.add(customer);
                }
            }
        }

        return customerList;
    }

    private void saveToStorage(String fileName, Customer customer) {
        try {
            byte[] data = objectMapper.writeValueAsBytes(customer);
            storage.create(
                Blob.newBuilder(bucketName, fileName).build(),
                data
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to save customer to storage", e);
        }
    }

    private Customer fetchFromStorage(String fileName) {
        try {
            Blob blob = storage.get(bucketName, fileName);
            if (blob == null) {
                return null;
            }
            byte[] data = blob.getContent();
            return objectMapper.readValue(data, Customer.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch customer from storage", e);
        }
    }
}
