package com.example.Models;

import java.util.UUID;

public class Event {
    public UUID id;
    public String name;
    public String location;
    public String date;
    public int eventCapacity;


    public Event() {}

    public Event(String name, String location, String date, int eventCapacity) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.location = location;
        this.date = date;
        this.eventCapacity = eventCapacity;
    }
}

