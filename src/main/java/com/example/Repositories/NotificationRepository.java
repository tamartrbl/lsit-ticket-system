package com.example.Repositories;

import com.example.Models.Notification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Repository
public class NotificationRepository {
    static HashMap<UUID, Notification> notifications = new HashMap<>();

    // Add a new notification
    public void add(Notification notification) {
        notification.id = UUID.randomUUID();
        notifications.put(notification.id, notification);
    }

    // Retrieve a notification by ID
    public Notification get(UUID id) {
        return notifications.get(id);
    }

    // Remove a notification by ID
    public void remove(UUID id) {
        notifications.remove(id);
    }

    // Update an existing notification (e.g., status or message)
    public void update(Notification notification) {
        Notification existingNotification = notifications.get(notification.id);
        if (existingNotification != null) {
            existingNotification.message = notification.message;
            existingNotification.status = notification.status;
        }
    }

    // List all notifications
    public List<Notification> list() {
        return new ArrayList<>(notifications.values());
    }

    // List notifications by Customer ID
    public List<Notification> listByCustomerId(UUID customerId) {
        List<Notification> customerNotifications = new ArrayList<>();
        for (Notification notification : notifications.values()) {
            if (notification.customerId.equals(customerId)) {
                customerNotifications.add(notification);
            }
        }
        return customerNotifications;
    }
}

