package com.example.Repositories;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
            // Hardcoded credentials in JSON format as a string
            String credentialsJson = """
            {
                "type": "service_account",
                "project_id": "lsit-442916",
                "private_key_id": "0c2068cf4c1d206e436ad647412759d87000d954",
                "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC2r1hoYHKos4cC\nWp4uRU7icDs5b0/92Ra1cCi3XwYfLZRLimo5LU7Inmm0y/zNuqs2hUTVHMFZ6XAC\nBGYR4BWf4OdaX0MjyV99Oe4AcHpEht+NxAhwwQ6a7LHHjd6rVswepPHowghsDZGb\ntVZHwjCDkvYpltimvXPJgERGq6DwCO7swWCN57cWqLcyoGgGojt4zMtvY4uwm0W6\ntXmdsoNz3Ncn92+efy6a71N3MlS0o0pFEyPEK5dutskD88q1S6AiHPQNkMdiA4gN\nvqBXsPzjs93if2JZdpZi2eTycG4IOYNa66OoyNvYKzt7ySeh9s5CeES5dcbjhoTY\nH3lIgoAJAgMBAAECggEAB6s1cb8/vPF5XMnPS1e1O6f0soDn2ZsF+ySbzvPOGtuz\n1pZ3RlJutNkCjLiF8qBwDMQ8KxqPis3T+f+zhbNTkRA0wgxO8hcDW0tF4I6YMV18\nj7PPm2UdTvdZ8Ku10dk9TRmFPq07g4avyrqqZi2CkhlmnEEU8+3uyQIJi7JreXwC\nWDtmTPvKCvWheUpQrOoBNOzV/dyliO8EK7YQB/P++PZJoF6p2Hu3ksLYmQWXGlKh\nu57UGjH8B3STuifmfi2tR+qUtQG+/pzpUkCtnnSNz7k6rntBDtTovwdWzRsKh6n+\n5RkpkuysX9HvzKGbc784tKRUOkqB0UAtcqoOfANkEQKBgQDhygw5HHOnr+vN1W3+\n1TS/AJqOV8s5MiPZ7Slcl/gAte6lNymWimUlMUo86re+QFoacQ68BPM7X/7PrDj7\necNreQ4NsNbiCxJZQnF8XLm+zlnmJpdqYSYhIIiFfG8ihpV4tNrb0SLaK/lxgQ4K\nSvD5ZmNrix8Ir5KHNuXcUtbI2QKBgQDPINjcLJ7rkICkjw7YOvXg+R3zAPJxH8rG\nbUYpX6lfZbofsAkdPNpfKbOp+uFUaA0F4mlS17495AVRndVK3oKLfkdESngJnr27\nDliQJ0oUzN8B2HTBAK4zl8F1kO9thXoRvDfqpc6qhINuhoFGRgNJ3+2jg2P7NcGn\naGSCHnlysQKBgFZsXONbVZWAy7Q8ll6tQwQ8+eu0BRB+daSLl4JelMnI2XCZvaxm\nirKCvFS2zt/eh96JGcez6Vn3s3PQCvuOrtMrQcrRvl5FOJ+7w+6DG4HCQDzM1rYc\nO1kAt+DIMU24z+uk5gSvdzSlElR0vmgIKVZ8718RoC5V7bMduzvq5VV5AoGAbyg+\nmXJs2xiSoT2RBuQlflXTB81zTthz9yc/pwaKnAK47zaGvaUhwD5eCBP2h1jL3GOU\niHv1wbs0S4e7ptFhF8FuN60LXIZPZiqdxsb/D8ieyZvDlKxr0I5ZqWFdzNX8G7Z5\n/7h5/7Lb4h3XFMKQEz5UhxMFPhSsV85DWj1GihECgYEAlS2UBMMogOr7c+tsSagG\njp1lPY4b+xAQWh5q70XOmqbwdDe251pwyHVkVret4IxVjo8SNhK+0SzzPbF7FWrK\nPCwRaiwnXHX9On7pTtaAC/hMISqB5RU/VT4hZTQkNglxqgdD3jTTK5Cnn6koY6k2\njukr94JEkZ8ldplIr1WECjo=\n-----END PRIVATE KEY-----\n",
                "client_email": "lsit-706-848@lsit-442916.iam.gserviceaccount.com",
                "client_id": "114820746903836873397",
                "auth_uri": "https://accounts.google.com/o/oauth2/auth",
                "token_uri": "https://oauth2.googleapis.com/token",
                "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
                "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/lsit-706-848%40lsit-442916.iam.gserviceaccount.com",
                "universe_domain": "googleapis.com"
            }
            """;
            GoogleCredentials credentials = ServiceAccountCredentials.fromStream(
                new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8))
            );

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
