package com.rental.CarRental.service;

import com.rental.CarRental.dto.CreateCarRequest;
import com.rental.CarRental.entity.Car;
import com.rental.CarRental.entity.RentalCompany;
import com.rental.CarRental.exception.ResourceNotFoundException;
import com.rental.CarRental.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final RentalCompanyService rentalCompanyService;

    public CarService(CarRepository carRepository, RentalCompanyService rentalCompanyService) {
        this.carRepository = carRepository;
        this.rentalCompanyService = rentalCompanyService;
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Car getCarByLicensePlate(String licensePlate) {
        return carRepository.findById(licensePlate).
                orElseThrow(() -> new ResourceNotFoundException("Car with license plate " + licensePlate + " not found"));
    }

    public List<Car> getCarsByRentalCompany(UUID rentalCompanyId) {
        rentalCompanyService.getRentalCompanyById(rentalCompanyId);
        return carRepository.findAllByRentalCompanyId(rentalCompanyId);
    }

    public Car createCar(UUID rentalCompanyId, CreateCarRequest request) {
        RentalCompany rentalCompany = rentalCompanyService.getRentalCompanyById(rentalCompanyId);

        Car car = new Car(request.licensePlate(),
                request.brand(),
                request.seats(),
                request.primaryColor(),
                request.secondaryColor(),
                rentalCompany);

        return carRepository.save(car);
    }

    public void deleteCar(String licensePlate) {
        Car car = this.getCarByLicensePlate(licensePlate);
        carRepository.delete(car);
    }
}
