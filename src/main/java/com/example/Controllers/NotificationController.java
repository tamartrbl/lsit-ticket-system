package com.example.Controllers;

import com.example.Models.Notification;
import com.example.Models.Payment;
import com.example.Models.Ticket;
import com.example.Repositories.NotificationRepository;
import com.example.Repositories.PaymentRepository;
import com.example.Repositories.TicketRepository;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationRepository notificationRepository;
    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;

    public NotificationController(NotificationRepository notificationRepository, TicketRepository ticketRepository, PaymentRepository paymentRepository) {
        this.notificationRepository = notificationRepository;
        this.ticketRepository = ticketRepository;
        this.paymentRepository = paymentRepository;
    }

    @PostMapping("/newEvent")
    public String notifyNewEvent(@RequestParam String message) {
        notificationRepository.notifyAllCustomers(message);
        return "All customers have been notified of the new event.";
    }

    @PostMapping("/paymentStatus")
    public String notifyPaymentStatus(@RequestParam UUID paymentId) {
        Payment payment = paymentRepository.get(paymentId);
        if (payment == null) {
            return "Payment not found!";
        }

        String notificationMessage = switch (payment.state) {
            case COMPLETED -> "Your payment has been completed. A ticket has been issued.";
            case FAILED -> "Your payment has failed. Please try again.";
            case CANCELLED -> "Your payment cannot be refunded.";
            default -> "Payment status is unknown.";
        };

        notificationRepository.addNotification(new Notification(payment.customer, notificationMessage));
        return "Customer has been notified about the payment status.";
    }

    @PostMapping("/refundStatus")
    public String notifyRefundStatus(@RequestParam UUID ticketId) {
        Ticket ticket = ticketRepository.get(ticketId);
        if (ticket == null) {
            return "Ticket not found!";
        }

        String message;
        if (ticket.state == Ticket.TicketState.CANCELLED) {
            message = "Your refund has been processed successfully.";
        } else {
            message = "Refund not possible for this ticket.";
        }

        notificationRepository.addNotification(new Notification(ticket.customer.id, message));
        return "Customer has been notified about the refund status.";
    }
}
