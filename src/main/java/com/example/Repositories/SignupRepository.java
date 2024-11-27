package com.example.Repositories;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class SignupRepository implements ISignupRepository {
    private final Map<UUID, List<UUID>> customerEventMap = new HashMap<>();

    public void signup(UUID customerId, UUID eventId) {
        customerEventMap.computeIfAbsent(customerId, k -> new ArrayList<>()).add(eventId);
    }

    public void leave(UUID customerId, UUID eventId) {
        List<UUID> eventIds = customerEventMap.get(customerId);
        if (eventIds != null) {
            eventIds.remove(eventId);
        }
    }

    public List<UUID> getSignedUpEvents(UUID customerId) {
        return customerEventMap.getOrDefault(customerId, Collections.emptyList());
    }
}
