package com.rental.CarRental.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateRentalCompanyRequest(
        @NotBlank(message = "Name cannot be empty") String name,
        @NotBlank(message = "Address cannot be empty") String address,
        String email,
        String phoneNumber
) {
}
