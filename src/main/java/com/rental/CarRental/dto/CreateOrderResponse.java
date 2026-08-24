package com.rental.CarRental.dto;

import com.rental.CarRental.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateOrderResponse(
        UUID orderId,
        String licensePlate,
        UUID rentalCompanyId,
        LocalDateTime orderedAt,
        OrderStatus orderStatus
) {
}
