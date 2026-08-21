package com.rental.CarRental.service.security.authorization;

import com.rental.CarRental.dto.UpdateOrderRequest;
import com.rental.CarRental.entity.Order;
import com.rental.CarRental.exception.ForbiddenAccessException;
import com.rental.CarRental.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderAuthorizationService {

    private final OrderService orderService;

    public OrderAuthorizationService(OrderService orderService) {
        this.orderService = orderService;
    }

    public void isOwnerOfOrder(UUID orderId, UpdateOrderRequest updateOrderRequest) {
        Order order = orderService.getOrderById(orderId);
        if(!order.getCustomer().getId().equals(updateOrderRequest.customerId())) {
            throw new ForbiddenAccessException("Cannot change someone else their order");
        }
    }
}
