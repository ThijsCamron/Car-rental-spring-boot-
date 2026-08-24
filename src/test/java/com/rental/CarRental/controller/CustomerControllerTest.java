package com.rental.CarRental.controller;

import com.rental.CarRental.dto.CreateCustomerRequest;
import com.rental.CarRental.entity.Customer;
import com.rental.CarRental.exception.ResourceAlreadyExistsException;
import com.rental.CarRental.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private com.rental.CarRental.service.security.CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private com.rental.CarRental.service.security.TokenBlackListService tokenBlackListService;

    @MockitoBean
    private com.rental.CarRental.repository.PlatformAdminRepository platformAdminRepository;

    @Test
    void createCustomerPassesCorrectRequestToService() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "Jan", "Jansen", "Straat 1", "jan@example.com"
        );

        Customer customer = new Customer("Jan", "Jansen", "Straat 1", "jan@example.com");
        when(customerService.createCustomer(any(CreateCustomerRequest.class))).thenReturn(customer);

        mockMvc.perform(post("/customers")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Jansen"))
                .andExpect(jsonPath("$.email").value("jan@example.com"));

        ArgumentCaptor<CreateCustomerRequest> captor = ArgumentCaptor.forClass(CreateCustomerRequest.class);
        verify(customerService).createCustomer(captor.capture());
        assertThat(captor.getValue().firstName()).isEqualTo("Jan");
        assertThat(captor.getValue().lastName()).isEqualTo("Jansen");
        assertThat(captor.getValue().email()).isEqualTo("jan@example.com");
    }

    @Test
    void createCustomerWithDuplicateEmailReturns400() throws Exception {
        when(customerService.createCustomer(any(CreateCustomerRequest.class)))
                .thenThrow(new ResourceAlreadyExistsException("Customer with email jan@example.com already exists"));

        mockMvc.perform(post("/customers")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCustomerRequest("Jan", "Jansen", "Straat 1", "jan@example.com")
                        )))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Customer with email jan@example.com already exists"));
    }

    @Test
    void unauthenticatedRequestReturns401() throws Exception {
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCustomerRequest("Jan", "Jansen", "Straat 1", "jan@example.com")
                        )))
                .andExpect(status().isUnauthorized());
    }
}
