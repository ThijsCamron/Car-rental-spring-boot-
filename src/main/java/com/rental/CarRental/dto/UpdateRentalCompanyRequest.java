package com.rental.CarRental.dto;

import jakarta.validation.constraints.Size;

public record UpdateRentalCompanyRequest(
        @Size(min = 1, message = "Name cannot be empty") String name,
        @Size(min = 1, message = "Address cannot be empty") String address,
        String email,
        String phoneNumber
) {
}
