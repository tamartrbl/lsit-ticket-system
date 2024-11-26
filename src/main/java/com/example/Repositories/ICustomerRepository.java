package com.example.Repositories;

import com.example.Models.Customer;

import java.util.List;
import java.util.UUID;

public interface ICustomerRepository {
    void add(Customer customer);
    Customer get(UUID id);
    void remove(UUID id);
    void update(Customer customer);
    List<Customer> list();
}

