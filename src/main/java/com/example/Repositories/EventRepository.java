package com.example.Repositories;

import com.example.Models.Customer;
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
}
