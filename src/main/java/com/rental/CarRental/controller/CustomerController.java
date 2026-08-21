package com.rental.CarRental.controller;

import com.rental.CarRental.dto.CreateCustomerRequest;
import com.rental.CarRental.entity.Customer;
import com.rental.CarRental.service.CustomerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public Customer createCustomer(@RequestBody CreateCustomerRequest request) {
        return customerService.createCustomer(request);
    }
}
