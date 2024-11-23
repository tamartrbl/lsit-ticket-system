package com.example.Repositories;

import com.example.Models.Payment;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.UUID;

@Repository
public class PaymentRepository {
    private final HashMap<UUID, Payment> payments = new HashMap<>();

    public void add(Payment payment) {
        payments.put(payment.getId(), payment);
    }

    public Payment getByTicketId(UUID ticketId) {
        return payments.values().stream()
                .filter(payment -> ticketId.equals(payment.getEventId()))
                .findFirst()
                .orElse(null);
    }

    public Payment get(UUID id) {
        return payments.get(id);
    }

    public void update(Payment payment) {
        payments.put(payment.getId(), payment);
    }

    public boolean processRefund(UUID paymentId) {
        Payment payment = payments.get(paymentId);
        if (payment == null) {
            return false;
        }

        // Simulate refund logic
        // In a real application, integrate with a payment provider (e.g., iDEAL)
        payment.setStatus("REFUNDED");
        update(payment);
        return true;
    }
}
