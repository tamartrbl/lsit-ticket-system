package com.example.Repositories;

import static com.example.Models.Ticket.TicketState.CANCELLED;
import static com.example.Models.Ticket.TicketState.FROZEN;
import static com.example.Models.Ticket.TicketState.ISSUED;
import static com.example.Models.Ticket.TicketState.NOT_ISSUED;
import static com.example.Models.Ticket.TicketState.SCANNED;

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

import com.example.Models.Event;
import com.example.Models.Ticket;
import com.example.Models.Ticket.TicketState;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
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
