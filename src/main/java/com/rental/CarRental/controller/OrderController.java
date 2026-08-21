package com.rental.CarRental.controller;

import com.rental.CarRental.dto.UpdateOrderRequest;
import com.rental.CarRental.dto.UpdateOrderResponse;
import com.rental.CarRental.service.OrderService;
import com.rental.CarRental.service.security.authorization.OrderAuthorizationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderAuthorizationService orderAuthorizationService;

    public OrderController(OrderService orderService, OrderAuthorizationService orderAuthorizationService) {
        this.orderService = orderService;
        this.orderAuthorizationService = orderAuthorizationService;
    }

    @PreAuthorize("@orderAuthorizationService.isOwnerOfOrder(#id, request)")
    @PatchMapping("/{id}")
    public UpdateOrderResponse patchOrder(@PathVariable UUID id, @RequestBody UpdateOrderRequest request) {
        return orderService.updateOrder(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable UUID id) {
        orderService.deleteOrder(id);
    }



}
