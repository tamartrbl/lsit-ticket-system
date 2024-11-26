package com.example.Repositories;

import com.example.Models.Event;
import com.example.Models.Event;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Repository
public class EventRepository {
    private final HashMap<UUID, Event> events = new HashMap<>();

    public void add(Event event) {
        event.id = UUID.randomUUID();
        events.put(event.id, event);
    }

    public Event get(UUID id) {
        return events.get(id);
    }

    public List<Event> list() {
        return new ArrayList<>(events.values());
    }

    public void remove(UUID id) {
        events.remove(id);
    }

    public void update(Event event) {
        Event existingEvent = events.get(event.id);
        if (existingEvent != null) {
            existingEvent.name = event.name;
            existingEvent.location = event.location;
            existingEvent.date = event.date;
            existingEvent.ticketAvailable = event.eventCapacity;
            existingEvent.price = event.price;
        }
    }

}
