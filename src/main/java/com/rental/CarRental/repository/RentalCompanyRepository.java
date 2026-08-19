package com.rental.CarRental.repository;

import com.rental.CarRental.entity.RentalCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RentalCompanyRepository extends JpaRepository<RentalCompany, UUID> {
}
