package com.rental.CarRental.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCarRequest(
        @NotBlank(message = "License plate cannot be empty") String licensePlate,
        @NotBlank(message = "Brand cannot be empty") String brand,
        @NotNull(message = "Seats cannot be null") int seats,
        String primaryColor,
        String secondaryColor
) {
}
