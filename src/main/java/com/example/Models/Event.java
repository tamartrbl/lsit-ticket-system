package com.example.Models;

import java.util.UUID;

public class Event {
    public UUID id;
    public String name;
    public String location;
    public String date;
    public final int eventCapacity;
    public int ticketAvailable;
    public double price;

    public Event(String name, String location, String date, int eventCapacity, double price) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.location = location;
        this.date = date;
        this.eventCapacity = eventCapacity;
        this.ticketAvailable = eventCapacity;
        this.price = price;

    }
}

