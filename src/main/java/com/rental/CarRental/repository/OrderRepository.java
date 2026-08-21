package com.rental.CarRental.repository;

import com.rental.CarRental.entity.Car;
import com.rental.CarRental.entity.Order;
import com.rental.CarRental.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    boolean existsOrderByOrderStatusAndCar(OrderStatus orderStatus, Car car);
}
