package com.example.Repositories;

import static com.example.Models.Ticket.TicketState.CANCELLED;
import static com.example.Models.Ticket.TicketState.FROZEN;
import static com.example.Models.Ticket.TicketState.ISSUED;
import static com.example.Models.Ticket.TicketState.NOT_ISSUED;
import static com.example.Models.Ticket.TicketState.SCANNED;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.example.Models.Event;
import com.example.Models.Ticket;
import com.example.Models.Ticket.TicketState;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Repository
public class GTicketRepository implements ITicketRepository {
    private final String bucketName = "lsit-ticketing-bucket"; // Replace with your GCS bucket name
    private final String ticketFolder = "tickets/";  // Folder to store customer data
    private final Storage storage;
    private final ObjectMapper objectMapper;

    public GTicketRepository() {
        this.storage = initializeStorage();
        this.objectMapper = new ObjectMapper();
    }

    private Storage initializeStorage() {
        try {
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

            return StorageOptions.newBuilder()
                    .setCredentials(ServiceAccountCredentials.fromStream(new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8))))
                    .build()
                    .getService();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Google Cloud Storage", e);
        }
    }

    @Override
    public void add(Ticket ticket) {
        ticket.id = UUID.randomUUID();
        saveToStorage(ticket);
    }

    @Override
    public void update(Ticket ticket) {
        saveToStorage(ticket);
    }

    @Override
    public void issue(Ticket ticket) {
        if (ticket.state == NOT_ISSUED || ticket.state == CANCELLED) {
            ticket.state = ISSUED;
            saveToStorage(ticket);
        }
    }

    @Override
    public void freeze(Ticket ticket) {
        if (ticket.state == ISSUED || ticket.state == CANCELLED || ticket.state == SCANNED) {
            ticket.state = FROZEN;
            saveToStorage(ticket);
        }
    }

    @Override
    public void unfreeze(Ticket ticket) {
        if (ticket.state == FROZEN) {
            ticket.state = ISSUED;
            saveToStorage(ticket);
        }
    }

    @Override
    public void scan(Ticket ticket) {
        if (ticket.state == ISSUED) {
            ticket.state = SCANNED;
            saveToStorage(ticket);
        }
    }

    @Override
    public void cancel(Ticket ticket) {
        if (ticket.state == ISSUED || ticket.state == FROZEN) {
            ticket.state = CANCELLED;
            saveToStorage(ticket);
        }
    }

    @Override
    public List<Ticket> getEventTickets(Event event) {
        return listTicketsForEvent(event, null);
    }

    @Override
    public List<Ticket> getEventTicketsByStatus(Event event, TicketState state) {
        return listTicketsForEvent(event, state);
    }

    @Override
    public void generateEventTickets(Event event) {
        for (int i = 0; i < event.eventCapacity; i++) {
            Ticket ticket = new Ticket();
            ticket.event = event;
            ticket.state = NOT_ISSUED;
            add(ticket);
        }
    }

    @Override
    public Ticket get(UUID id) {
        return fetchFromStorage(ticketFolder + id + ".json");
    }

    @Override
    public List<Ticket> list() {
        List<Ticket> tickets = new ArrayList<>();
        Bucket bucket = storage.get(bucketName);

        for (Blob blob : bucket.list(Storage.BlobListOption.prefix(ticketFolder)).iterateAll()) {
            if (blob.getName().endsWith(".json")) {
                Ticket ticket = fetchFromStorage(blob.getName());
                if (ticket != null) {
                    tickets.add(ticket);
                }
            }
        }

        return tickets;
    }

    @Override
    public void remove(UUID id) {
        storage.delete(bucketName, ticketFolder + id + ".json");
    }

    private void saveToStorage(Ticket ticket) {
        try {
            byte[] data = objectMapper.writeValueAsBytes(ticket);
            storage.create(Blob.newBuilder(bucketName, ticketFolder + ticket.id + ".json").build(), data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save ticket to storage", e);
        }
    }

    private Ticket fetchFromStorage(String fileName) {
        try {
            Blob blob = storage.get(bucketName, fileName);
            if (blob == null) return null;
            return objectMapper.readValue(blob.getContent(), Ticket.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch ticket from storage", e);
        }
    }

    private List<Ticket> listTicketsForEvent(Event event, TicketState state) {
        List<Ticket> tickets = new ArrayList<>();
        for (Ticket ticket : list()) {
            if (ticket.event.id.equals(event.id) && (state == null || ticket.state == state)) {
                tickets.add(ticket);
            }
        }
        return tickets;
    }
}
