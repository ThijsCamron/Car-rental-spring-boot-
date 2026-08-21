package com.rental.CarRental.dto;

import com.rental.CarRental.entity.OrderStatus;

import java.util.UUID;

public record UpdateOrderResponse(
        UUID orderId,
        String licensePlate,
        OrderStatus orderStatus
) {

}
