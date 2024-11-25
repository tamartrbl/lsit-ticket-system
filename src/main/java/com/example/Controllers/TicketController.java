package com.example.Controllers;

import com.example.Models.Customer;
import com.example.Models.Event;
import com.example.Models.Payment;
import com.example.Models.Ticket;
import com.example.Repositories.PaymentRepository;
import com.example.Repositories.TicketRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;
    
    public TicketController(TicketRepository ticketRepository, PaymentRepository paymentRepository) {
        this.ticketRepository = ticketRepository;
        this.paymentRepository = paymentRepository;
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
    
    @PostMapping("/{ticketId}/refund")
    public String refund(@PathVariable UUID ticketId) {
        Ticket ticket = ticketRepository.get(ticketId);
        if (ticket == null) {
            return "Ticket not found!";
        }

        // Validate if ticket is refundable
        if (!ticket.isRefundable()) {
            return "Refund not applicable for this ticket!";
        }

        // Fetch associated payment using customer and event IDs
        Payment payment = paymentRepository.getByCustomerAndEvent(ticket.customer.id, ticket.event.id);
        if (payment == null) {
            return "Payment not found for the ticket!";
        }

        // Ensure the payment state is COMPLETED
        if (payment.state != Payment.PaymentState.COMPLETED) {
            return "Payment is not eligible for refund!";
        }

        // Freeze the ticket while processing the refund
        ticketRepository.freeze(ticket);

        // Process the refund
        boolean refundSuccess = paymentRepository.processRefund(payment.id);

        if (refundSuccess) {
            // Mark the ticket as CANCELLED if the refund is successful
            ticket.state = Ticket.TicketState.CANCELLED;
            ticketRepository.update(ticket);

            // Update the payment state to REFUNDED
            payment.state = Payment.PaymentState.FAILED; // Use FAILED as a refund marker (or adjust if needed)
            paymentRepository.update(payment);

            return "Refund processed successfully!";
        } else {
            // Keep the ticket frozen if refund fails
            ticket.state = Ticket.TicketState.FROZEN;
            ticketRepository.update(ticket);

            return "Refund failed! Ticket remains frozen.";
        }
    }
}
