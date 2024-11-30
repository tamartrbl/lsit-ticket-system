package com.example.Controllers;

        import com.example.Models.Customer;
        import com.example.Models.Event;
        import com.example.Models.Payment;
        import com.example.Models.Ticket;
        import com.example.Repositories.*;
        import org.springframework.web.bind.annotation.*;

        import java.util.List;
        import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private final GCustomerRepository customerRepository;
    private final GEventRepository eventRepository;
    private final SignupRepository signupRepository;
    private final PaymentRepository paymentRepository; // Add this
    private final GTicketRepository ticketRepository;

    public CustomerController(GCustomerRepository customerRepository, GEventRepository eventRepository, SignupRepository signupRepository, PaymentRepository paymentRepository,
    GTicketRepository ticketRepository) {
        this.customerRepository = customerRepository;
        this.eventRepository = eventRepository;
        this.signupRepository = signupRepository;
        this.paymentRepository = paymentRepository;
        this.ticketRepository = ticketRepository;
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

        if (event.ticketAvailable > 0){
            signupRepository.signup(customerId, eventId);
        } else {
            return "No more spots available!";
        }

        List<UUID> signedUpEvents = signupRepository.getSignedUpEvents(customerId);
        if (!signedUpEvents.contains(eventId)) {
            return "Customer is not signed up for the event!";
        }

        // Randomized probability for payment success (80% success rate)
        boolean paymentSuccess = Math.random() < 0.8;
        //FIRST CREATE THE TICKET AND ADD TO REPO WITH SOME (RANDOM) PRICE, THEN CREATE THE PAYMENT
        //AND ADD TO REPO AND SIMULATE IT
        //THEN ISSUE THE TICKET
        if (paymentSuccess) {
            // Create a successful payment record
            Payment payment = new Payment(customerId, eventId, event.price); // Replace 100.0 with actual event price
            payment.state = Payment.PaymentState.COMPLETED;

            // Issue the ticket
            Ticket ticket = new Ticket(event, customer, payment.amount);

            // Add the payment and ticket records
            paymentRepository.add(payment);
            ticketRepository.add(ticket);

            event.ticketAvailable--;

            eventRepository.update(event);

            return "Payment successful! Ticket issued.";
        } else {
            // Create a failed payment record
            Payment payment = new Payment(customerId, eventId, event.price); // Replace 100.0 with actual event price
            payment.state = Payment.PaymentState.FAILED;

            // Add the failed payment record
            paymentRepository.add(payment);

            return "Payment failed. Please try again.";
        }
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

