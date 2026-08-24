package com.rental.CarRental.service;

import com.rental.CarRental.dto.CreateAdminRequest;
import com.rental.CarRental.dto.CreateAdminResponse;
import com.rental.CarRental.dto.UpdateAdminRequest;
import com.rental.CarRental.dto.UpdateAdminResponse;
import com.rental.CarRental.entity.Admin;
import com.rental.CarRental.entity.RentalCompany;
import com.rental.CarRental.exception.ResourceNotFoundException;
import com.rental.CarRental.repository.AdminRepository;
import com.rental.CarRental.repository.RentalCompanyRepository;
import com.rental.CarRental.service.security.TokenBlackListService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final RentalCompanyService rentalCompanyService;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlackListService tokenBlackListService;

    public AdminService(AdminRepository adminRepository, RentalCompanyService rentalCompanyService,
                        PasswordEncoder passwordEncoder,
                        TokenBlackListService tokenBlackListService) {
        this.adminRepository = adminRepository;
        this.rentalCompanyService = rentalCompanyService;
        this.passwordEncoder = passwordEncoder;
        this.tokenBlackListService = tokenBlackListService;
    }

    public Admin getAdminByEmail(String email) {
        return adminRepository.findByEmail(email).
                orElseThrow(() -> new ResourceNotFoundException("Admin with email " + email + " cannot be found"));
    }

    public CreateAdminResponse createAdmin(UUID rentalCompanyId, CreateAdminRequest request) {
        String hashedPassword = passwordEncoder.encode(request.password());
        RentalCompany rentalCompany = rentalCompanyService.getRentalCompanyById(rentalCompanyId);

        Admin admin = new Admin(request.email(), hashedPassword, request.firstName(), request.lastName(), rentalCompany);
        adminRepository.save(admin);

        return new CreateAdminResponse(admin.getEmail(), admin.getFirstName(), admin.getLastName());
    }

    public UpdateAdminResponse updateAdmin(UUID id, UpdateAdminRequest request) {
        Admin admin = adminRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Admin with id " + id + " cannot be found"));

        if(request.email() != null) {
            admin.setEmail(request.email());
        }

        if(request.password() != null) {
            String encodedPassword = passwordEncoder.encode(request.password());
            admin.setPassword(encodedPassword);
        }

        if(request.firstName() != null) {
            admin.setFirstName(request.firstName());
        }

        if(request.lastName() != null) {
            admin.setLastName(request.lastName());
        }

        adminRepository.save(admin);
        return new UpdateAdminResponse(admin.getEmail(), admin.getFirstName(), admin.getLastName());
    }

    public void updateRefreshToken(String email, String refreshToken) {
        //TODO custom error message here, not throw 404
        Admin admin = this.getAdminByEmail(email);

        admin.setRefreshToken(refreshToken);
        adminRepository.save(admin);
    }


}
