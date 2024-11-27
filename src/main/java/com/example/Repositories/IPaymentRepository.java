package com.example.Repositories;

import com.example.Models.Payment;

import java.util.HashMap;
import java.util.UUID;

public interface IPaymentRepository {
    void add(Payment payment);
    Payment get(UUID id);
    Payment getByCustomerAndEvent(UUID customerId, UUID eventId);
    void update(Payment payment);
    boolean processRefund(UUID paymentId);
    HashMap<UUID, Payment> list();
}
