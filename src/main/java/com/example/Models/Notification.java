package com.example.Models;

import java.util.UUID;

public class Notification {
    public UUID id;
    public UUID customerId;
    public String message;
    public String timestamp;

    public Notification(UUID customerId, String message) {
        this.id = UUID.randomUUID();
        this.customerId = customerId;
        this.message = message;
        this.timestamp = java.time.Instant.now().toString();
    }
}
