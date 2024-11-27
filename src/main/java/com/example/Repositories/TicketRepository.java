package com.example.Repositories;
import com.example.Models.Event;
import com.example.Models.Ticket;
import com.example.Models.Ticket.TicketState;

import org.springframework.stereotype.Repository;
import java.util.*;
import static com.example.Models.Ticket.TicketState.*;

@Repository
public class TicketRepository implements ITicketRepository {
    private final HashMap<UUID, Ticket> tickets = new HashMap<>();

    public void add(Ticket ticket) {
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

    public void unfreeze(Ticket ticket) {
        Ticket existingTicket = tickets.get(ticket.id);
        if (existingTicket != null && existingTicket.state == FROZEN ) {
            existingTicket.state = ISSUED;
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

    public List<Ticket> getEventTickets(Event event){
        List<Ticket> allEventTickets = new ArrayList<>();

        for (Ticket ticket : tickets.values()) {
            if (ticket.event.id.equals(event.id)) {
                allEventTickets.add(ticket);
            }
        }
        return allEventTickets;
    }

    public List<Ticket> getEventTicketsByStatus(Event event, TicketState state){
        List<Ticket> allEventTickets = new ArrayList<>();

        for (Ticket ticket : tickets.values()) {
            if (ticket.event.id.equals(event.id) && ticket.state.equals(state)) {
                allEventTickets.add(ticket);
            }
        }
        return allEventTickets;
    }


    public void generateEventTickets(Event event){
        int numOfTickets = event.eventCapacity;

        for(int i = 0; i < numOfTickets; i++){
            Ticket ticket = new Ticket();
            add(ticket);
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
