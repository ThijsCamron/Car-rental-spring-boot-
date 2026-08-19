package com.rental.CarRental.controller;

import com.rental.CarRental.entity.Car;
import com.rental.CarRental.service.CarService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public List<Car> getAllCars() {
        return carService.getAllCars();
    }

    @GetMapping("/{licensePlate}")
    public Car getCarById(@PathVariable String licensePlate) {
        return carService.getCarByLicensePlate(licensePlate);
    }

    @DeleteMapping("/{licensePlate}")
    public void deleteCarById(@PathVariable String licensePlate) {
        carService.deleteCar(licensePlate);
    }
}
