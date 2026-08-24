package com.rental.CarRental.entity;

import com.rental.CarRental.service.OrderService;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt = LocalDateTime.now();

    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus = OrderStatus.IN_PROGRESS;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @ManyToOne
    @JoinColumn(name = "rental_company_id")
    private RentalCompany rentalCompany;

    protected Order(){}

    public Order(Customer customer, Car car, RentalCompany rentalCompany) {
        this.customer = customer;
        this.car = car;
        this.rentalCompany = rentalCompany;
    }

    public UUID getId() {
        return id;
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Car getCar() {
        return car;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
