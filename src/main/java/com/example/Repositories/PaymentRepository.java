package com.example.Repositories;

import com.example.Models.Payment;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.UUID;

@Repository
public class PaymentRepository implements IPaymentRepository {
    private final HashMap<UUID, Payment> payments = new HashMap<>();

    // Add a new payment
    public void add(Payment payment) {
        payments.put(payment.id, payment);
    }

    // Retrieve a payment by its ID
    public Payment get(UUID id) {
        return payments.get(id);
    }

    // Retrieve a payment by customer and event ID
    public Payment getByCustomerAndEvent(UUID customerId, UUID eventId) {
        return payments.values().stream()
                .filter(payment -> payment.customer.equals(customerId) && payment.event.equals(eventId))
                .findFirst()
                .orElse(null);
    }

    // Update a payment (by replacing the existing payment with the same ID)
    public void update(Payment payment) {
        if (payments.containsKey(payment.id)) {
            payments.put(payment.id, payment);
        }
    }

    // Process a refund for a given payment ID
    public boolean processRefund(UUID paymentId) {
        Payment payment = payments.get(paymentId);
        if (payment == null || payment.state != Payment.PaymentState.COMPLETED) {
            return false;
        }

        // Simulate refund logic: Update payment state to CANCELLED
        payment.state = Payment.PaymentState.CANCELLED;
        payments.put(payment.id, payment); // Update in the repository
        return true;
    }

    // List all payments
    public HashMap<UUID, Payment> list() {
        return payments;
    }
}
