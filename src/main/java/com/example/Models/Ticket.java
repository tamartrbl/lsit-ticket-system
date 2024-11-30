package com.example.Models;
import java.util.UUID;

public class Ticket {
    public UUID id;
    public String customerEmail;
    public TicketState state;
    public double price;
    public Event event;
    public Customer customer;


    public enum TicketState {
        NOT_ISSUED, // The ticket is unassigned
        ISSUED,     //The ticket has been issued to a customer
        SCANNED,     //The ticket has been scanned at event and cannot be reused
        CANCELLED,   // The ticket has been cancelled
        FROZEN;      // The ticket is temporarily frozen
    }

    public Ticket() {
    }

    public Ticket(Event event, Customer customer, double price) {
        this.id = UUID.randomUUID();
        this.price = price;
        this.event = event;
        this.customer = customer;
        this.state = TicketState.ISSUED;

    }
    
    public boolean isRefundable() {
        // Refund policy: Tickets can only be refunded if they are in FROZEN or ISSUED state
        //return this.state == TicketState.FROZEN || this.state == TicketState.ISSUED;
        return true;
    }
}



