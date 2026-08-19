package com.rental.CarRental.dto;

import jakarta.validation.constraints.Size;

public record UpdateAdminRequest(
        @Size(min = 1, message = "Email cannot be empty") String email,
        @Size(min = 1, message = "Password cannot be empty") String password,
        String firstName,
        String lastName
) {
}
