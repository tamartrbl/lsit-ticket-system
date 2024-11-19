package com.example.Models;

import java.util.UUID;

public class Notification {
    public UUID id;
    public UUID customerId; // Link to the customer who receives the notification
    public String message; // Notification message
    public String status; // Status of the notification (e.g., SENT, FAILED)

    // Default constructor
    public Notification() {}

    // Constructor with parameters
    public Notification(UUID customerId, String message, String status) {
        this.id = UUID.randomUUID(); // Auto-generate unique ID
        this.customerId = customerId;
        this.message = message;
        this.status = status;
    }

    // Optional: Getter and Setter methods (if needed for encapsulation)
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
