package com.rental.CarRental.service.security;

import com.rental.CarRental.entity.Admin;
import com.rental.CarRental.entity.PlatformAdmin;
import com.rental.CarRental.repository.AdminRepository;
import com.rental.CarRental.repository.PlatformAdminRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final PlatformAdminRepository platformAdminRepository;

    public CustomUserDetailsService(AdminRepository adminRepository, PlatformAdminRepository platformAdminRepository) {
        this.adminRepository = adminRepository;
        this.platformAdminRepository = platformAdminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {

        Optional<Admin> admin = adminRepository.findByEmail(id);
        if(admin.isPresent()) {
            return admin.get();
        }

        Optional<PlatformAdmin> platformAdmin = platformAdminRepository.findById(UUID.fromString(id));
        if(platformAdmin.isPresent()) {
            return platformAdmin.get();
        }

        throw new UsernameNotFoundException("User not found " + id);
    }
}
