package com.example.Controllers;

import com.example.Models.Event;
import com.example.Repositories.*;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events")
public class EventController {
    private final GCustomerRepository customerRepository;
    private final GEventRepository eventRepository;
    private final SignupRepository signupRepository;
    private final GPaymentRepository paymentRepository;
    private final GTicketRepository ticketRepository;

    public EventController(GCustomerRepository customerRepository, GEventRepository eventRepository, SignupRepository signupRepository, GPaymentRepository paymentRepository,
    GTicketRepository ticketRepository) {
        this.customerRepository = customerRepository;
        this.eventRepository = eventRepository;
        this.signupRepository = signupRepository;
        this.paymentRepository = paymentRepository;
        this.ticketRepository = ticketRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('EventManager', 'Customer')")
    public List<Event> list() {
        return eventRepository.list();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EventManager', 'Customer')")
    public Event get(@PathVariable UUID id) {
        return eventRepository.get(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('EventManager')")
    public Event add(@RequestBody Event event) {
        eventRepository.add(event);
        return event;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EventManager')")
    public void delete(@PathVariable UUID id) {
        eventRepository.remove(id);
    }
}
