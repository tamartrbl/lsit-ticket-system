package com.example.Models;

import java.util.UUID;

public class Payment {
    public UUID id; // Unique identifier for the payment
    public UUID customer; // Reference to the customer who made the payment
    public UUID event; // Reference to the event the payment is for
    public PaymentState state; // Status of the payment: "PENDING", "COMPLETED", "FAILED"
    public double amount; // Payment amount
    public String timestamp; // Timestamp of the payment creation or update

    public Payment() {}
    
    public enum PaymentState {
        PENDING,
        COMPLETED,
        FAILED,
        CANCELLED;
    }

    public Payment(UUID customer, UUID event, double amount) {
        this.id = UUID.randomUUID();
        this.customer = customer;
        this.event = event;
        this.amount = amount;
        this.state = PaymentState.PENDING;
        this.timestamp = java.time.Instant.now().toString();
    }

    public Payment(UUID customer, UUID event, double amount, PaymentState status) {
        this.id = UUID.randomUUID();
        this.customer = customer;
        this.event = event;
        this.amount = amount;
        this.state = status;
        this.timestamp = java.time.Instant.now().toString();
    }

    
}
