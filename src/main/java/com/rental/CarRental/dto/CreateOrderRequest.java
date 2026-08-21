package com.rental.CarRental.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateOrderRequest(
        @NotNull(message = "Customer id cannot be null") UUID customerId,
        @Size(min = 5, max = 10, message = "License plate cannot have less than 5 and more than 10 characters") String licensePlate
) {
}
