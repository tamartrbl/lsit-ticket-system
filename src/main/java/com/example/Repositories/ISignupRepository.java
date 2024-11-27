package com.example.Repositories;

import java.util.List;
import java.util.UUID;

public interface ISignupRepository {
    void signup(UUID customerId, UUID eventId);
    void leave(UUID customerId, UUID eventId);
    List<UUID> getSignedUpEvents(UUID customerId);
}
