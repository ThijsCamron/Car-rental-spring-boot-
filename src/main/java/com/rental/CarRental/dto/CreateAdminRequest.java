package com.rental.CarRental.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateAdminRequest(
        @NotBlank(message = "Email cannot be empty") String email,
        @NotBlank(message = "Password cannot be empty") String password,
        String firstName,
        String lastName
) {
}
