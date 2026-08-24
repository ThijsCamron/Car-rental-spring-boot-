package com.rental.CarRental.service.security.authorization;

import com.rental.CarRental.exception.ForbiddenAccessException;
import com.rental.CarRental.repository.AdminRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RentalCompanyAuthorizationService {

    //TODO wijzigen naar adminService i.v.m. cross-dependency
    private final AdminRepository adminRepository;

    public RentalCompanyAuthorizationService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public void isAdminOfRentalCompany(UUID rentalCompanyId, Authentication authentication) {
        if(!adminRepository.existsByEmailAndRentalCompany_Id(authentication.getName(), rentalCompanyId)) {
            throw new ForbiddenAccessException("Unauthorized to edit this rental company");
        }
    }
}
