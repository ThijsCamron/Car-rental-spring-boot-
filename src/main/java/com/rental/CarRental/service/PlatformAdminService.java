package com.rental.CarRental.service;

import com.rental.CarRental.entity.PlatformAdmin;
import com.rental.CarRental.exception.ResourceNotFoundException;
import com.rental.CarRental.repository.PlatformAdminRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PlatformAdminService {

    private final PlatformAdminRepository platformAdminRepository;

    public PlatformAdminService(PlatformAdminRepository platformAdminRepository) {
        this.platformAdminRepository = platformAdminRepository;
    }

    public PlatformAdmin getAdminById(UUID id) {
        return platformAdminRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Platform admin with id " + id + " cannot be found"));
    }

    public void updateRefreshToken(UUID id, String refreshToken) {
        //TODO custom error message here, not throw 404
        PlatformAdmin platformAdmin = this.getAdminById(id);

        platformAdmin.setRefreshToken(refreshToken);
        platformAdminRepository.save(platformAdmin);
    }
}
