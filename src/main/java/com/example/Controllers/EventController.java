package com.example.Controllers;

import com.example.Models.Event;
import com.example.Repositories.EventRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping
    public List<Event> list() {
        return eventRepository.list();
    }

    @GetMapping("/{id}")
    public Event get(@PathVariable UUID id) {
        return eventRepository.get(id);
    }

    @PostMapping
    public Event add(@RequestBody Event event) {
        eventRepository.add(event);
        return event;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        eventRepository.remove(id);
    }
}
