package com.example.Repositories;

import com.example.Models.Event;
import com.example.Models.Ticket;

import java.util.List;
import java.util.UUID;

public interface ITicketRepository {
    void add(Ticket ticket);
    Ticket get(UUID id);
    List<Ticket> list();
    void remove(UUID id);
    void update(Ticket ticket);
    void issue(Ticket ticket);
    void freeze(Ticket ticket);
    void unfreeze(Ticket ticket);
    void cancel(Ticket ticket);
    void scan(Ticket ticket);
    List<Ticket> getEventTickets(Event event);
    List<Ticket> getEventTicketsByStatus(Event event, Ticket.TicketState state);
    void generateEventTickets(Event event);
}
