package com.rental.CarRental;

import com.rental.CarRental.entity.PlatformAdmin;
import com.rental.CarRental.repository.PlatformAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CarRentalApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarRentalApplication.class, args);
	}

	@Autowired
	PlatformAdminRepository platformAdminRepository;

	@Bean
	public CommandLineRunner startup(@Value("${platform-admin.password}") String password) {

		return args -> {
			if(platformAdminRepository.findAll().isEmpty()) {
				PlatformAdmin platformAdmin = new PlatformAdmin(password);
				platformAdminRepository.save(platformAdmin);
			}
		};
	}

}
