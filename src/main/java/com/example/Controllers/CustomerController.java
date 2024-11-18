package com.example.Controllers;

        import com.example.Models.Customer;
        import com.example.Models.Event;
        import com.example.Repositories.CustomerRepository;
        import com.example.Repositories.EventRepository;
        import com.example.Repositories.SignupRepository;
        import org.springframework.web.bind.annotation.*;

        import java.util.List;
        import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerRepository customerRepository;
    private final EventRepository eventRepository;
    private final SignupRepository signupRepository;

    public CustomerController(CustomerRepository customerRepository, EventRepository eventRepository, SignupRepository signupRepository) {
        this.customerRepository = customerRepository;
        this.eventRepository = eventRepository;
        this.signupRepository = signupRepository;
    }

    @GetMapping
    public List<Customer> list() {
        return customerRepository.list();
    }

    @GetMapping("/{id}")
    public Customer get(@PathVariable UUID id) {
        return customerRepository.get(id);
    }

    @PostMapping
    public Customer add(@RequestBody Customer customer) {
        customerRepository.add(customer);
        return customer;
    }

    @PutMapping("/{id}")
    public Customer update(@PathVariable UUID id, @RequestBody Customer customer) {
        customer.id = id;
        customerRepository.update(customer);
        return customer;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        customerRepository.remove(id);
    }

    @PostMapping("/{customerId}/signup/{eventId}")
    public String signup(@PathVariable UUID customerId, @PathVariable UUID eventId) {
        Customer customer = customerRepository.get(customerId);
        Event event = eventRepository.get(eventId);

        if (customer == null || event == null) {
            return "Customer or Event not found!";
        }

        signupRepository.signup(customerId, eventId);
        return "Customer signed up for the event successfully!";
    }

    @PostMapping("/{customerId}/leave/{eventId}")
    public String leave(@PathVariable UUID customerId, @PathVariable UUID eventId) {
        signupRepository.leave(customerId, eventId);
        return "Customer removed from the event!";
    }

    @GetMapping("/{customerId}/events")
    public List<UUID> getSignedUpEvents(@PathVariable UUID customerId) {
        return signupRepository.getSignedUpEvents(customerId);
    }
}

