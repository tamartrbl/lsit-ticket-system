package com.example.Models;
import java.util.UUID;

public class Ticket {
    public UUID id;
    public String customer_email;
    private TicketState state;
    public double price;

    enum TicketState {
        NOT_ISSUED, // The ticket is unassigned
        ISSUED,     //The ticket has been issued to a customer
        SCANNED,     //The ticket has been scanned at event and cannot be reused
        CANCELLED,   // The ticket has been cancelled
        FROZEN;      // The ticket is temporarily frozen
    }

    public Ticket() {}

    public Ticket(String customer_email, double price) {
        this.id = UUID.randomUUID();
        this.price = price;
        this.customer_email = customer_email;
        this.state = TicketState.NOT_ISSUED;

    }
}



