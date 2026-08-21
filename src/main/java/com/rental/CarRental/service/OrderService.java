package com.rental.CarRental.service;

import com.rental.CarRental.dto.CreateOrderRequest;
import com.rental.CarRental.dto.CreateOrderResponse;
import com.rental.CarRental.dto.UpdateOrderRequest;
import com.rental.CarRental.dto.UpdateOrderResponse;
import com.rental.CarRental.entity.*;
import com.rental.CarRental.exception.CarAlreadyInUseException;
import com.rental.CarRental.exception.OrderStatusException;
import com.rental.CarRental.exception.ResourceNotFoundException;
import com.rental.CarRental.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CarService carService;
    private final CustomerService customerService;
    private final RentalCompanyService rentalCompanyService;

    public OrderService(OrderRepository orderRepository,
                        CarService carService,
                        CustomerService customerService,
                        RentalCompanyService rentalCompanyService) {
        this.orderRepository = orderRepository;
        this.carService = carService;
        this.customerService = customerService;
        this.rentalCompanyService = rentalCompanyService;
    }

    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId).
                orElseThrow(() -> new ResourceNotFoundException("Order with id " + orderId + " cannot be found"));
    }

    public CreateOrderResponse createOrder(UUID rentalCompanyId, CreateOrderRequest request) {
        Customer customer = customerService.getCustomerById(request.customerId());
        Car car = carService.getCarByLicensePlate(request.licensePlate());
        RentalCompany rentalCompany = rentalCompanyService.getRentalCompanyById(rentalCompanyId);
        Order order = new Order(customer, car, rentalCompany);

        orderRepository.save(order);
        return new CreateOrderResponse(order.getId(),
                car.getLicensePlate(),
                rentalCompanyId,
                order.getOrderedAt(),
                order.getOrderStatus());
    }

    public UpdateOrderResponse updateOrder(UUID id, UpdateOrderRequest updateOrderRequest) {
        Order order = this.getOrderById(id);
        if(updateOrderRequest.licensePlate() != null) {

            if(updateOrderRequest.status() != null) {
                throw new OrderStatusException("Cannot change car and status in the same request");
            }

            Car car = carService.getCarByLicensePlate(updateOrderRequest.licensePlate());
            if(orderRepository.existsOrderByOrderStatusAndCar(OrderStatus.IN_USE, car)) {
                throw new CarAlreadyInUseException("Car with license plate " + updateOrderRequest.licensePlate() + " is already in use");
            }

            order.setCar(car);
            orderRepository.save(order);
        }

        if(updateOrderRequest.status() != null) {
            order.setOrderStatus(updateOrderRequest.status());
        }

        return new UpdateOrderResponse(id, order.getCar().getLicensePlate(), order.getOrderStatus());
    }

    public void deleteOrder(UUID id) {
        Order order = this.getOrderById(id);
        orderRepository.delete(order);
    }

}
