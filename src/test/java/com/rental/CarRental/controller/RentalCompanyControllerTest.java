package com.rental.CarRental.controller;

import com.rental.CarRental.dto.*;
import com.rental.CarRental.entity.Car;
import com.rental.CarRental.entity.OrderStatus;
import com.rental.CarRental.entity.RentalCompany;
import com.rental.CarRental.exception.ResourceNotFoundException;
import com.rental.CarRental.service.AdminService;
import com.rental.CarRental.service.CarService;
import com.rental.CarRental.service.OrderService;
import com.rental.CarRental.service.RentalCompanyService;
import com.rental.CarRental.service.security.authorization.RentalCompanyAuthorizationService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RentalCompanyController.class)
public class RentalCompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RentalCompanyService rentalCompanyService;

    @MockitoBean
    private RentalCompanyAuthorizationService rentalCompanyAuthorizationService;

    @MockitoBean
    private CarService carService;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private com.rental.CarRental.service.security.CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private com.rental.CarRental.service.security.TokenBlackListService tokenBlackListService;

    @MockitoBean
    private com.rental.CarRental.repository.PlatformAdminRepository platformAdminRepository;

    @Test
    void getAllRentalCompaniesReturnsList() throws Exception {
        RentalCompany company1 = new RentalCompany("Hertz", "Amsterdam");
        RentalCompany company2 = new RentalCompany("Europcar", "Rotterdam");

        when(rentalCompanyService.getAllRentalCompanies()).thenReturn(List.of(company1, company2));

        mockMvc.perform(get("/rental-companies").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Hertz"))
                .andExpect(jsonPath("$[1].name").value("Europcar"));
    }

    @Test
    void createRentalCompanyPassesCorrectRequest() throws Exception {
        CreateRentalCompanyRequest request = new CreateRentalCompanyRequest(
                "Hertz", "Amsterdam", "info@hertz.nl", "0201234567"
        );

        RentalCompany company = new RentalCompany("Hertz", "Amsterdam");
        company.setEmail("info@hertz.nl");
        company.setPhoneNumber("0201234567");

        when(rentalCompanyService.createRentalCompany(any(CreateRentalCompanyRequest.class))).thenReturn(company);

        mockMvc.perform(post("/rental-companies")
                        .with(jwt().jwt(j -> j.claim("scope", "ROLE_PLATFORM_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hertz"))
                .andExpect(jsonPath("$.address").value("Amsterdam"))
                .andExpect(jsonPath("$.email").value("info@hertz.nl"));

        ArgumentCaptor<CreateRentalCompanyRequest> captor = ArgumentCaptor.forClass(CreateRentalCompanyRequest.class);
        verify(rentalCompanyService).createRentalCompany(captor.capture());
        assertThat(captor.getValue().name()).isEqualTo("Hertz");
        assertThat(captor.getValue().address()).isEqualTo("Amsterdam");
    }

    @Test
    void deleteRentalCompanySucceeds() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/rental-companies/" + id)
                        .with(jwt().jwt(j -> j.claim("scope", "ROLE_PLATFORM_ADMIN"))))
                .andExpect(status().isOk());

        verify(rentalCompanyService).deleteRentalCompany(id);
    }

    @Test
    void deleteRentalCompanyNotFoundReturns404() throws Exception {
        UUID id = UUID.randomUUID();

        org.mockito.Mockito.doThrow(new ResourceNotFoundException("Rental company with id " + id + " not found"))
                .when(rentalCompanyService).deleteRentalCompany(id);

        mockMvc.perform(delete("/rental-companies/" + id)
                        .with(jwt().jwt(j -> j.claim("scope", "ROLE_PLATFORM_ADMIN"))))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Rental company with id " + id + " not found"));
    }

    @Test
    void patchRentalCompanyPassesCorrectRequest() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateRentalCompanyRequest request = new UpdateRentalCompanyRequest(
                "Hertz Updated", null, "new@hertz.nl", null
        );

        RentalCompany updated = new RentalCompany("Hertz Updated", "Amsterdam");
        updated.setEmail("new@hertz.nl");

        when(rentalCompanyService.patchRentalCompany(eq(id), any(UpdateRentalCompanyRequest.class))).thenReturn(updated);

        mockMvc.perform(patch("/rental-companies/" + id)
                        .with(jwt().jwt(j -> j.claim("scope", "ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hertz Updated"))
                .andExpect(jsonPath("$.email").value("new@hertz.nl"));

        ArgumentCaptor<UpdateRentalCompanyRequest> captor = ArgumentCaptor.forClass(UpdateRentalCompanyRequest.class);
        verify(rentalCompanyService).patchRentalCompany(eq(id), captor.capture());
        assertThat(captor.getValue().name()).isEqualTo("Hertz Updated");
    }

    @Test
    void getCarsByRentalCompanyReturnsList() throws Exception {
        UUID id = UUID.randomUUID();
        RentalCompany company = new RentalCompany("Hertz", "Amsterdam");
        Car car = new Car("AB-123-CD", "Toyota", 5, "Red", "Black", company);

        when(carService.getCarsByRentalCompany(id)).thenReturn(List.of(car));

        mockMvc.perform(get("/rental-companies/" + id + "/cars").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].licensePlate").value("AB-123-CD"));
    }

    @Test
    void addCarToRentalCompanyPassesCorrectRequest() throws Exception {
        UUID id = UUID.randomUUID();
        CreateCarRequest request = new CreateCarRequest("AB-123-CD", "Toyota", 5, "Red", "Black");
        RentalCompany company = new RentalCompany("Hertz", "Amsterdam");
        Car car = new Car("AB-123-CD", "Toyota", 5, "Red", "Black", company);

        when(carService.createCar(eq(id), any(CreateCarRequest.class))).thenReturn(car);

        mockMvc.perform(post("/rental-companies/" + id + "/cars")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate").value("AB-123-CD"))
                .andExpect(jsonPath("$.brand").value("Toyota"));

        ArgumentCaptor<CreateCarRequest> captor = ArgumentCaptor.forClass(CreateCarRequest.class);
        verify(carService).createCar(eq(id), captor.capture());
        assertThat(captor.getValue().licensePlate()).isEqualTo("AB-123-CD");
    }

    @Test
    void createOrderPassesCorrectRequest() throws Exception {
        UUID rentalCompanyId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        CreateOrderRequest request = new CreateOrderRequest(customerId, "AB-123-CD");
        CreateOrderResponse response = new CreateOrderResponse(
                orderId, "AB-123-CD", rentalCompanyId, LocalDateTime.now(), OrderStatus.IN_PROGRESS
        );

        when(orderService.createOrder(eq(rentalCompanyId), any(CreateOrderRequest.class))).thenReturn(response);

        mockMvc.perform(post("/rental-companies/" + rentalCompanyId + "/orders")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.licensePlate").value("AB-123-CD"))
                .andExpect(jsonPath("$.orderStatus").value("IN_PROGRESS"));

        ArgumentCaptor<CreateOrderRequest> captor = ArgumentCaptor.forClass(CreateOrderRequest.class);
        verify(orderService).createOrder(eq(rentalCompanyId), captor.capture());
        assertThat(captor.getValue().customerId()).isEqualTo(customerId);
        assertThat(captor.getValue().licensePlate()).isEqualTo("AB-123-CD");
    }

    @Test
    void addAdminToRentalCompanyPassesCorrectRequest() throws Exception {
        UUID id = UUID.randomUUID();
        CreateAdminRequest request = new CreateAdminRequest(
                "admin@hertz.nl", "password123", "Admin", "User"
        );
        CreateAdminResponse response = new CreateAdminResponse("admin@hertz.nl", "Admin", "User");

        when(adminService.createAdmin(eq(id), any(CreateAdminRequest.class))).thenReturn(response);

        mockMvc.perform(post("/rental-companies/" + id + "/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@hertz.nl"))
                .andExpect(jsonPath("$.firstName").value("Admin"));

        ArgumentCaptor<CreateAdminRequest> captor = ArgumentCaptor.forClass(CreateAdminRequest.class);
        verify(adminService).createAdmin(eq(id), captor.capture());
        assertThat(captor.getValue().email()).isEqualTo("admin@hertz.nl");
    }

    @Test
    void unauthenticatedGetReturns401() throws Exception {
        mockMvc.perform(get("/rental-companies"))
                .andExpect(status().isUnauthorized());
    }
}
