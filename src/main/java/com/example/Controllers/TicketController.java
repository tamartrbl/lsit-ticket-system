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

    public TicketController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @GetMapping
    public List<Ticket> list() {
        return ticketRepository.list();
    }

    @GetMapping("/{id}")
    public Ticket get(@PathVariable UUID id) {
        return ticketRepository.get(id);
    }

    @PostMapping
    public Ticket add(@RequestBody Ticket ticket) {
        ticketRepository.add(ticket);
        return ticket;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        ticketRepository.remove(id);
    }

    @PutMapping("/{id}")
    public Ticket update(@PathVariable UUID id, @RequestBody Ticket ticket) {
        ticket.id = id;
        ticketRepository.update(ticket);
        return ticket;
    }

}
