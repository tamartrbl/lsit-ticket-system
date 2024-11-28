package com.example.Repositories;

import com.example.Models.Customer;
import com.example.Models.Notification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Repository
public class NotificationRepository {
    private final HashMap<UUID, Notification> notifications = new HashMap<>();
    private final CustomerRepository customerRepository;

    public NotificationRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void addNotification(Notification notification) {
        notifications.put(notification.id, notification);
    }

    public Notification getNotification(UUID id) {
        return notifications.get(id);
    }

    public List<Notification> listNotifications() {
        return new ArrayList<>(notifications.values());
    }

    public void notifyAllCustomers(String message) {
        for (Customer customer : customerRepository.list()) {
            Notification notification = new Notification(customer.id, message);
            addNotification(notification);
        }
    }
}
