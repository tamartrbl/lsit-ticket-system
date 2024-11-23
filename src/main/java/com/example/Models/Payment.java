package com.example.Models;

import java.util.UUID;

public class Payment {
    public UUID id; // Unique identifier for the payment
    public UUID customerId; // Reference to the customer who made the payment
    public UUID eventId; // Reference to the event the payment is for
    public String status; // Status of the payment: "PENDING", "COMPLETED", "FAILED"
    public String method; // Payment method, e.g., "iDEAL"
    public double amount; // Payment amount
    public String timestamp; // Timestamp of the payment creation or update

    public Payment() {}

    public Payment(UUID customerId, UUID eventId, String method, double amount) {
        this.id = UUID.randomUUID();
        this.customerId = customerId;
        this.eventId = eventId;
        this.method = method;
        this.amount = amount;
        this.status = "PENDING";
        this.timestamp = java.time.Instant.now().toString();
    }
