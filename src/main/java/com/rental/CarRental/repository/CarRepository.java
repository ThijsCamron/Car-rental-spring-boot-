package com.rental.CarRental.repository;

import com.rental.CarRental.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CarRepository extends JpaRepository<Car, String> {
    List<Car> findAllByBrand(String brand);
    List<Car> findAllByRentalCompanyId(UUID rentalCompanyId);
}
