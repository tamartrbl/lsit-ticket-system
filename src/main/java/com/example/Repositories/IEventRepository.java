package com.example.Repositories;

import com.example.Models.Event;

import java.util.List;
import java.util.UUID;

public interface IEventRepository {
    void add(Event event);
    Event get(UUID id);
    List<Event> list();
    void remove(UUID id);
    void update(Event event);
}
