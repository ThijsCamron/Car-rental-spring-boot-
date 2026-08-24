package com.rental.CarRental.service;

import com.rental.CarRental.dto.CreateCustomerRequest;
import com.rental.CarRental.entity.Customer;
import com.rental.CarRental.exception.ResourceAlreadyExistsException;
import com.rental.CarRental.exception.ResourceNotFoundException;
import com.rental.CarRental.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer getCustomerById(UUID uuid) {
        return customerRepository.findById(uuid).
                orElseThrow(() -> new ResourceNotFoundException("Customer with id " + uuid + " cannot be found"));
    }

    public Customer createCustomer(CreateCustomerRequest request) {
        if(customerRepository.existsByEmail(request.email())) {
            throw new ResourceAlreadyExistsException("Customer with email " + request.email() + " already exists");
        }

        Customer customer = new Customer(request.firstName(), request.lastName(), request.address(), request.email());
        return customerRepository.save(customer);
    }
}
