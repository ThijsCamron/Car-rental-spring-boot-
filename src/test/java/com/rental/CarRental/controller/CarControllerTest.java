package com.rental.CarRental.controller;

import com.rental.CarRental.entity.Car;
import com.rental.CarRental.entity.RentalCompany;
import com.rental.CarRental.exception.ResourceNotFoundException;
import com.rental.CarRental.service.CarService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarController.class)
public class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarService carService;

    @MockitoBean
    private com.rental.CarRental.service.security.CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private com.rental.CarRental.service.security.TokenBlackListService tokenBlackListService;

    @MockitoBean
    private com.rental.CarRental.repository.PlatformAdminRepository platformAdminRepository;

    @Test
    void getAllCarsReturnsListOfCars() throws Exception {
        RentalCompany company = new RentalCompany("TestCompany", "TestAddress");
        Car car1 = new Car("AB-123-CD", "Toyota", 5, "Red", "Black", company);
        Car car2 = new Car("EF-456-GH", "BMW", 4, "Blue", null, company);

        when(carService.getAllCars()).thenReturn(List.of(car1, car2));

        mockMvc.perform(get("/cars").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].licensePlate").value("AB-123-CD"))
                .andExpect(jsonPath("$[0].brand").value("Toyota"))
                .andExpect(jsonPath("$[1].licensePlate").value("EF-456-GH"));
    }

    @Test
    void getCarByLicensePlateReturnsCar() throws Exception {
        RentalCompany company = new RentalCompany("TestCompany", "TestAddress");
        Car car = new Car("AB-123-CD", "Toyota", 5, "Red", "Black", company);

        when(carService.getCarByLicensePlate("AB-123-CD")).thenReturn(car);

        mockMvc.perform(get("/cars/AB-123-CD").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate").value("AB-123-CD"))
                .andExpect(jsonPath("$.brand").value("Toyota"))
                .andExpect(jsonPath("$.seats").value(5));
    }

    @Test
    void getCarByLicensePlateNotFoundReturns404() throws Exception {
        when(carService.getCarByLicensePlate("XX-000-XX"))
                .thenThrow(new ResourceNotFoundException("Car with license plate XX-000-XX not found"));

        mockMvc.perform(get("/cars/XX-000-XX").with(jwt()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Car with license plate XX-000-XX not found"));
    }

    @Test
    void deleteCarPassesCorrectLicensePlateToService() throws Exception {
        mockMvc.perform(delete("/cars/AB-123-CD").with(jwt()))
                .andExpect(status().isOk());

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(carService).deleteCar(captor.capture());
        assertThat(captor.getValue()).isEqualTo("AB-123-CD");
    }

    @Test
    void deleteCarNotFoundReturns404() throws Exception {
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("Car with license plate XX-000-XX not found"))
                .when(carService).deleteCar("XX-000-XX");

        mockMvc.perform(delete("/cars/XX-000-XX").with(jwt()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Car with license plate XX-000-XX not found"));
    }

    @Test
    void unauthenticatedRequestReturns401() throws Exception {
        mockMvc.perform(get("/cars"))
                .andExpect(status().isUnauthorized());
    }
}
