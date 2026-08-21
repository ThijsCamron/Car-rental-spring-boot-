package com.rental.CarRental.repository;

import com.rental.CarRental.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdminRepository extends JpaRepository<Admin, UUID> {

    boolean existsByEmailAndRentalCompany_Id(String email, UUID rentalCompanyId);
    Optional<Admin> findByEmail(String email);
}
