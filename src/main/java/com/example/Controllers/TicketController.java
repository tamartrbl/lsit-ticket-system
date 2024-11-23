package com.example.Controllers;

import com.example.Models.Customer;
import com.example.Models.Event;
import com.example.Models.Ticket;
import com.example.Repositories.TicketRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    private final TicketRepository ticketRepository;

    public EventController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @GetMapping
    public List<Event> list() {
        return ticketRepository.list();
    }

    @GetMapping("/{id}")
    public Event get(@PathVariable UUID id) {
        return ticketRepository.get(id);
    }

    @PostMapping
    public Event add(@RequestBody Ticket ticket) {
        ticketRepository.add(ticket);
        return ticket;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        ticketRepository.remove(id);
    }

    @
    public void update(@PathVariable UUID id) {
        ticketRepository.remove(id);
    }

    @PutMapping("/{id}")
    public Customer update(@PathVariable UUID id, @RequestBody Ticket ticket) {
        ticket.id = id;
        ticketRepository.update(ticket);
        return ticket;
    }

}
