package com.rental.CarRental.repository;

import com.rental.CarRental.entity.PlatformAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlatformAdminRepository extends JpaRepository<PlatformAdmin, UUID> {

}
