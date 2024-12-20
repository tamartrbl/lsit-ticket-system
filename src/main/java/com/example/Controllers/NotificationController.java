package com.example.Controllers;

import com.example.Models.Notification;
import com.example.Models.Payment;
import com.example.Models.Ticket;
import com.example.Repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final GNotificationRepository notificationRepository;
    private final GTicketRepository ticketRepository;
    private final GPaymentRepository paymentRepository;

    private final GCustomerRepository customerRepository;

    public NotificationController(GNotificationRepository notificationRepository, GTicketRepository ticketRepository, GPaymentRepository paymentRepository, GCustomerRepository customerRepository) {
        this.notificationRepository = notificationRepository;
        this.ticketRepository = ticketRepository;
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
    }

    @PostMapping("/notifyAllCustomers")
    @PreAuthorize("hasRole('Marketing')")
    public String notifyNewEvent(@RequestParam String message) {
        notificationRepository.notifyAllCustomers(message);
        return "All customers have been notified of the new event.";
    }

    @PostMapping("/paymentStatus")
    @PreAuthorize("hasRole('Marketing')")
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
    @PreAuthorize("hasRole('Marketing')")
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

    @GetMapping("/lastCustomerNotification")
    @PreAuthorize("hasRole('Marketing')")
    public Notification getCustomerLastNotification(@RequestParam UUID customerId) {
        Notification lastNotification = notificationRepository.getCustomerLastNotification(customerId);

        if (lastNotification == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No notifications found for the given customer.");
        }

        return lastNotification;
    }

    @PostMapping("/notifyPayments")
    @PreAuthorize("hasRole('Marketing')")
    public String notifyPayments(@RequestParam String startTime, @RequestParam String endTime, @RequestParam Payment.PaymentState status, @RequestParam int paymentType // 0 for payment, 1 for refund
    ) {
        // Parse the time range from String to instant object
        java.time.Instant start = java.time.Instant.parse(startTime);
        java.time.Instant end = java.time.Instant.parse(endTime);

        // Define predefined messages
        String normalPaymentMessage = "Your payment of $%s has been processed with status: %s.";
        String refundMessage = "Your refund of $%s has been processed with status: %s.";

        // Filter payments within the timeframe and status
        List<Payment> filteredPayments = paymentRepository.list().values().stream()
                .filter(payment -> {
                    java.time.Instant paymentTime = java.time.Instant.parse(payment.timestamp);
                    boolean withinTimeframe = !paymentTime.isBefore(start) && !paymentTime.isAfter(end);
                    boolean matchesStatus = payment.state == status;
                    boolean isRefund = (payment.amount < 0);
                    return withinTimeframe && matchesStatus && ((paymentType == 1) == isRefund);
                })
                .toList();

        // Generate notifications for each payment
        for (Payment payment : filteredPayments) {
            String message;
            if (paymentType == 1) {
                message = String.format(refundMessage, Math.abs(payment.amount), payment.state);
            } else {
                message = String.format(normalPaymentMessage, Math.abs(payment.amount), payment.state);
            }

            notificationRepository.addNotification(new Notification(payment.customer, message));
        }

        return String.format("Notifications generated for %d payments.", filteredPayments.size());
    }

    @GetMapping("/allCustomerNotifications")
    @PreAuthorize("hasRole('Marketing')")
    public List<Notification> getAllCustomerNotifications(@RequestParam UUID customerId) {
        List<Notification> allNotifications = notificationRepository.getAllNotificationsFromCustomer(customerId);
        if (allNotifications.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No notifications found for the given customer.");
        }
        return allNotifications;
    }

}
