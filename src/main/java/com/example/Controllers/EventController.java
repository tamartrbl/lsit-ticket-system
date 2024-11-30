package com.example.Controllers;

import com.example.Models.Event;
import com.example.Repositories.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events")
public class EventController {
    private final GCustomerRepository customerRepository;
    private final GEventRepository eventRepository;
    private final SignupRepository signupRepository;
    private final PaymentRepository paymentRepository;
    private final GTicketRepository ticketRepository;

    public EventController(GCustomerRepository customerRepository, GEventRepository eventRepository, SignupRepository signupRepository, PaymentRepository paymentRepository,
    GTicketRepository ticketRepository) {
        this.customerRepository = customerRepository;
        this.eventRepository = eventRepository;
        this.signupRepository = signupRepository;
        this.paymentRepository = paymentRepository;
        this.ticketRepository = ticketRepository;
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
