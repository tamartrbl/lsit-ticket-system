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
    private final PaymentRepository paymentRepository;
    
    public TicketController(TicketRepository ticketRepository, RefundService refundService) {
        this.ticketRepository = ticketRepository;
        this.refundService = refundService;
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

        if (!ticket.isRefundable()) {
            return "Refund not applicable for this ticket!";
        }

        // Fetch associated payment
        Payment payment = paymentRepository.getByTicketId(ticketId);
        if (payment == null || !"COMPLETED".equals(payment.getStatus())) {
            return "Payment not found or not eligible for refund!";
        }

        // Freeze the ticket during refund process
        ticket.state = Ticket.TicketState.FROZEN;
        ticketRepository.update(ticket);

        // Process refund
        boolean refundSuccess = paymentRepository.processRefund(payment.getId());

        if (refundSuccess) {
            ticket.state = Ticket.TicketState.CANCELLED; // Mark ticket as cancelled
            ticketRepository.update(ticket);

            payment.setStatus("REFUNDED"); // Update payment status
            paymentRepository.update(payment);

            return "Refund processed successfully!";
        } else {
            ticket.state = Ticket.TicketState.FROZEN; // Keep ticket frozen if refund fails
            ticketRepository.update(ticket);

            return "Refund failed! Ticket is frozen.";
        }
    }
}
