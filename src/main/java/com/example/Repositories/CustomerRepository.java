package com.example.Repositories;

import com.example.Models.Customer;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Repository
public class CustomerRepository {
    static HashMap<UUID, Customer> customers = new HashMap<>();

    public void add(Customer customer) {
        customer.id = UUID.randomUUID();
        customers.put(customer.id, customer);
    }

    public Customer get(UUID id) {
        return customers.get(id);
    }

    public void remove(UUID id) {
        customers.remove(id);
    }

    public void update(Customer customer) {
        Customer existingCustomer = customers.get(customer.id);
        if (existingCustomer != null) {
            existingCustomer.name = customer.name;
            existingCustomer.email = customer.email;
        }
    }

    public List<Customer> list() {
        return new ArrayList<>(customers.values());
    }
}
