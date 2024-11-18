package com.example.Models;

import java.util.UUID;

public class Customer {
    public UUID id;
    public String name;
    public String email;

    public Customer() {}

    public Customer(String name, String email) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
    }
}
