package com.example.Controllers;

import com.example.Models.Notification;
import com.example.Repositories.NotificationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // Get all notifications
    @GetMapping
    public List<Notification> list() {
        return notificationRepository.list();
    }

    // Get a specific notification by ID
    @GetMapping("/{id}")
    public Notification get(@PathVariable UUID id) {
        return notificationRepository.get(id);
    }

    // Add a new notification
    @PostMapping
    public Notification add(@RequestBody Notification notification) {
        notificationRepository.add(notification);
        return notification;
    }

    // Update an existing notification by ID
    @PutMapping("/{id}")
    public Notification update(@PathVariable UUID id, @RequestBody Notification notification) {
        notification.id = id;
        notificationRepository.update(notification);
        return notification;
    }

    // Delete a notification by ID
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        notificationRepository.remove(id);
    }

    // Get all notifications for a specific customer
    @GetMapping("/customer/{customerId}")
    public List<Notification> getNotificationsByCustomer(@PathVariable UUID customerId) {
        return notificationRepository.listByCustomerId(customerId);
    }
}