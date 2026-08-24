package com.rental.CarRental.dto;

import com.rental.CarRental.entity.OrderStatus;

import java.util.UUID;

public record UpdateOrderRequest(
        UUID customerId,
        String licensePlate,
        OrderStatus status
) {
}
