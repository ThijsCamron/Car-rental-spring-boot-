package com.rental.CarRental.controller;

import com.rental.CarRental.dto.*;
import com.rental.CarRental.entity.Car;
import com.rental.CarRental.entity.RentalCompany;
import com.rental.CarRental.service.AdminService;
import com.rental.CarRental.service.AuthService;
import com.rental.CarRental.service.CarService;
import com.rental.CarRental.service.RentalCompanyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/rental-companies")
public class RentalCompanyController {

    private final RentalCompanyService rentalCompanyService;
    private final CarService carService;
    private final AuthService authService;
    private final AdminService adminService;

    public RentalCompanyController(RentalCompanyService rentalCompanyService, CarService carService, AuthService authService, AdminService adminService) {
        this.rentalCompanyService = rentalCompanyService;
        this.carService = carService;
        this.authService = authService;
        this.adminService = adminService;
    }

    @GetMapping
    public List<RentalCompany> getAllRentalCompanies() {
        return rentalCompanyService.getAllRentalCompanies();
    }

    @GetMapping("/{id}")
    public RentalCompany getRentalCompanyById(UUID id) {
        return rentalCompanyService.getRentalCompanyById(id);
    }

    @PostMapping
    public RentalCompany createRentalCompany(@Valid @RequestBody CreateRentalCompanyRequest request) {
        return rentalCompanyService.createRentalCompany(request);
    }

    @DeleteMapping("/{id}")
    public void deleteRentalCompany(@PathVariable UUID id) {
        rentalCompanyService.deleteRentalCompany(id);
    }

    @PatchMapping("/{id}")
    public RentalCompany patchRentalCompany(@PathVariable UUID id, @Valid @RequestBody UpdateRentalCompanyRequest request) {
        return rentalCompanyService.patchRentalCompany(id, request);
    }

    @GetMapping("/{id}/cars")
    public List<Car> getCarsByRentalCompany(@PathVariable UUID id) {
        return carService.getCarsByRentalCompany(id);
    }

    @PostMapping("/{id}/cars")
    public Car addCarToRentalCompany(@PathVariable UUID id, @Valid @RequestBody CreateCarRequest request) {
        return carService.createCar(id, request);
    }

    @PostMapping("/{id}/admins")
    public CreateAdminResponse addAdminToRentalCompany(@PathVariable UUID id, @Valid @RequestBody CreateAdminRequest request) {
        return adminService.createAdmin(id, request);
    }
}
