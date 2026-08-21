package com.rental.CarRental.dto;

import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(
        @Size(min = 2, max = 30, message = "First name must be between 2 and 30 characters long") String firstName,
        @Size(min = 2, max = 30, message = "Last name must be between 2 and 30 characters long") String lastName,
        @Size(min = 1, max = 50, message = "Address cannot be empty") String address,
        @Size(min = 1, max = 100, message = "E-mail cannot be empty") String email
) {
}
