package com.rental.CarRental.service;

import com.rental.CarRental.dto.CreateCarRequest;
import com.rental.CarRental.entity.Car;
import com.rental.CarRental.entity.RentalCompany;
import com.rental.CarRental.exception.ResourceNotFoundException;
import com.rental.CarRental.repository.CarRepository;
import com.rental.CarRental.repository.RentalCompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final RentalCompanyRepository rentalCompanyRepository;

    public CarService(CarRepository carRepository, RentalCompanyRepository rentalCompanyRepository) {
        this.carRepository = carRepository;
        this.rentalCompanyRepository = rentalCompanyRepository;
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Car getCarByLicensePlate(String licensePlate) {
        return carRepository.findById(licensePlate).
                orElseThrow(() -> new ResourceNotFoundException("Car with license plate " + licensePlate + " not found"));
    }

    public List<Car> getCarsByRentalCompany(UUID rentalCompanyId) {
        if(!rentalCompanyRepository.existsById(rentalCompanyId)) {
            throw new ResourceNotFoundException("Rental company with id " + rentalCompanyId + " not found");
        }

        return carRepository.findAllByRentalCompanyId(rentalCompanyId);
    }

    public Car createCar(UUID rentalCompanyId, CreateCarRequest request) {
        RentalCompany rentalCompany = rentalCompanyRepository.findById(rentalCompanyId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental company with id " + rentalCompanyId + " not found"));

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
