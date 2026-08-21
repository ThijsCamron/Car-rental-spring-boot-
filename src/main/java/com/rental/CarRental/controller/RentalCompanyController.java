package com.rental.CarRental.controller;

import com.rental.CarRental.dto.*;
import com.rental.CarRental.entity.Car;
import com.rental.CarRental.entity.RentalCompany;
import com.rental.CarRental.service.*;
import com.rental.CarRental.service.security.authorization.RentalCompanyAuthorizationService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/rental-companies")
public class RentalCompanyController {

    private final RentalCompanyService rentalCompanyService;
    private final RentalCompanyAuthorizationService rentalCompanyAuthorizationService;
    private final CarService carService;
    private final OrderService orderService;
    private final AdminService adminService;

    public RentalCompanyController(RentalCompanyService rentalCompanyService,
                                   RentalCompanyAuthorizationService rentalCompanyAuthorizationService,
                                   CarService carService,
                                   OrderService orderService,
                                   AdminService adminService) {
        this.rentalCompanyService = rentalCompanyService;
        this.rentalCompanyAuthorizationService = rentalCompanyAuthorizationService;
        this.carService = carService;
        this.orderService = orderService;
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

    @PreAuthorize("@rentalCompanyAuthorizationService(#id, authentication)")
    @DeleteMapping("/{id}")
    public void deleteRentalCompany(@PathVariable UUID id) {
        rentalCompanyService.deleteRentalCompany(id);
    }

    @PreAuthorize("@rentalCompanyAuthorizationService(#id, authentication)")
    @PatchMapping("/{id}")
    public RentalCompany patchRentalCompany(@PathVariable UUID id, @Valid @RequestBody UpdateRentalCompanyRequest request) {
        return rentalCompanyService.patchRentalCompany(id, request);
    }

    @GetMapping("/{id}/cars")
    public List<Car> getCarsByRentalCompany(@PathVariable UUID id) {
        return carService.getCarsByRentalCompany(id);
    }

    @PreAuthorize("@rentalCompanyAuthorizationService(#id, authentication)")
    @PostMapping("/{id}/cars")
    public Car addCarToRentalCompany(@PathVariable UUID id, @Valid @RequestBody CreateCarRequest request) {
        return carService.createCar(id, request);
    }

    @PostMapping("/{id}/orders")
    public CreateOrderResponse createOrder(@PathVariable UUID id, @Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(id, request);
    }

    @PostMapping("/{id}/admins")
    public CreateAdminResponse addAdminToRentalCompany(@PathVariable UUID id, @Valid @RequestBody CreateAdminRequest request) {
        return adminService.createAdmin(id, request);
    }
}
