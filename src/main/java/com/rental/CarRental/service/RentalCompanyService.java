package com.rental.CarRental.service;

import com.rental.CarRental.dto.CreateRentalCompanyRequest;
import com.rental.CarRental.dto.UpdateRentalCompanyRequest;
import com.rental.CarRental.entity.RentalCompany;
import com.rental.CarRental.exception.ResourceNotFoundException;
import com.rental.CarRental.repository.RentalCompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RentalCompanyService {

    private final RentalCompanyRepository rentalCompanyRepository;

    public RentalCompanyService(RentalCompanyRepository rentalCompanyRepository) {
        this.rentalCompanyRepository = rentalCompanyRepository;
    }

    public List<RentalCompany> getAllRentalCompanies() {
        return rentalCompanyRepository.findAll();
    }

    public RentalCompany getRentalCompanyById(UUID id) {
        return rentalCompanyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental company with id " + id + " not found"));
    }

    public RentalCompany createRentalCompany(CreateRentalCompanyRequest request) {
        RentalCompany rentalCompany = new RentalCompany(request.name(), request.address());

        rentalCompany.setEmail(request.email());
        rentalCompany.setPhoneNumber(request.phoneNumber());

        return rentalCompanyRepository.save(rentalCompany);
    }

    public void deleteRentalCompany(UUID id) {
        RentalCompany rentalCompany = this.getRentalCompanyById(id);

        rentalCompanyRepository.delete(rentalCompany);
    }

    public RentalCompany patchRentalCompany(UUID id, UpdateRentalCompanyRequest request) {
        RentalCompany rentalCompany = this.getRentalCompanyById(id);

       if(request.name() != null) {
           rentalCompany.setName(request.name());
       }

       if(request.address() != null) {
           rentalCompany.setAddress(request.address());
       }

       if(request.email() != null) {
           rentalCompany.setEmail(request.email());
       }

       if(request.phoneNumber() != null) {
           rentalCompany.setPhoneNumber(request.phoneNumber());
       }

        return rentalCompanyRepository.save(rentalCompany);
    }
}
