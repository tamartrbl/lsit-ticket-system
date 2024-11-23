package com.example.Repositories;

import com.example.Models.Ticket;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.example.Models.Ticket.TicketState.*;

@Repository
public class TicketRepository {
    private final HashMap<UUID, Ticket> tickets = new HashMap<>();

    public void add(Ticket ticket) {
        ticket.id = UUID.randomUUID();
        tickets.put(ticket.id, ticket);
    }

    public void update(Ticket ticket) {
        Ticket existingTicket = tickets.get(ticket.id);
        if (existingTicket != null) {
            existingTicket.price = ticket.price;
            existingTicket.customerEmail = ticket.customerEmail;
            existingTicket.state = ticket.state;
        }
    }

    public void issue(Ticket ticket) {
        Ticket existingTicket = tickets.get(ticket.id);
        if (existingTicket != null && (existingTicket.state == NOT_ISSUED || existingTicket.state == CANCELLED)) {
            existingTicket.state = ISSUED;
        }
    }

    public void freeze(Ticket ticket) {
        Ticket existingTicket = tickets.get(ticket.id);
        if (existingTicket != null && (existingTicket.state == ISSUED || existingTicket.state == CANCELLED || existingTicket.state == SCANNED) ) {
            existingTicket.state = FROZEN;
        }
    }

    public void scan(Ticket ticket) {
        Ticket existingTicket = tickets.get(ticket.id);
        if (existingTicket != null && existingTicket.state == ISSUED) {
            existingTicket.state = SCANNED;
        }
    }

    public void cancel(Ticket ticket) {
        Ticket existingTicket = tickets.get(ticket.id);
        if (existingTicket != null && (existingTicket.state == ISSUED || existingTicket.state == FROZEN)) {
            existingTicket.state = CANCELLED;
        }
    }

    public Ticket get(UUID id) {
        return tickets.get(id);
    }

    public List<Ticket> list() {
        return new ArrayList<>(tickets.values());
    }

    public void remove(UUID id) {
        tickets.remove(id);
    }
}
