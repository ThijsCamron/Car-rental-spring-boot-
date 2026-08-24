package com.rental.CarRental.controller;

import com.rental.CarRental.dto.UpdateOrderRequest;
import com.rental.CarRental.dto.UpdateOrderResponse;
import com.rental.CarRental.entity.OrderStatus;
import com.rental.CarRental.exception.CarAlreadyInUseException;
import com.rental.CarRental.exception.OrderStatusException;
import com.rental.CarRental.exception.ResourceNotFoundException;
import com.rental.CarRental.service.OrderService;
import com.rental.CarRental.service.security.authorization.OrderAuthorizationService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private OrderAuthorizationService orderAuthorizationService;

    @MockitoBean
    private com.rental.CarRental.service.security.CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private com.rental.CarRental.service.security.TokenBlackListService tokenBlackListService;

    @MockitoBean
    private com.rental.CarRental.repository.PlatformAdminRepository platformAdminRepository;

    @Test
    void patchOrderPassesCorrectRequestToService() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        UpdateOrderRequest request = new UpdateOrderRequest(customerId, null, OrderStatus.IN_USE);
        UpdateOrderResponse response = new UpdateOrderResponse(orderId, "AB-123-CD", OrderStatus.IN_USE);

        when(orderService.updateOrder(eq(orderId), any(UpdateOrderRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/orders/" + orderId)
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.licensePlate").value("AB-123-CD"))
                .andExpect(jsonPath("$.orderStatus").value("IN_USE"));

        ArgumentCaptor<UpdateOrderRequest> captor = ArgumentCaptor.forClass(UpdateOrderRequest.class);
        verify(orderService).updateOrder(eq(orderId), captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(OrderStatus.IN_USE);
    }

    @Test
    void patchOrderNotFoundReturns404() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(orderService.updateOrder(eq(orderId), any(UpdateOrderRequest.class)))
                .thenThrow(new ResourceNotFoundException("Order with id " + orderId + " cannot be found"));

        mockMvc.perform(patch("/orders/" + orderId)
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UpdateOrderRequest(UUID.randomUUID(), null, OrderStatus.IN_USE)
                        )))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchOrderWithCarAndStatusReturns400() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(orderService.updateOrder(eq(orderId), any(UpdateOrderRequest.class)))
                .thenThrow(new OrderStatusException("Cannot change car and status in the same request"));

        mockMvc.perform(patch("/orders/" + orderId)
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UpdateOrderRequest(UUID.randomUUID(), "AB-123-CD", OrderStatus.IN_USE)
                        )))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot change car and status in the same request"));
    }

    @Test
    void patchOrderWithCarAlreadyInUseReturns400() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(orderService.updateOrder(eq(orderId), any(UpdateOrderRequest.class)))
                .thenThrow(new CarAlreadyInUseException("Car with license plate AB-123-CD is already in use"));

        mockMvc.perform(patch("/orders/" + orderId)
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UpdateOrderRequest(UUID.randomUUID(), "AB-123-CD", null)
                        )))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Car with license plate AB-123-CD is already in use"));
    }

    @Test
    void deleteOrderSucceeds() throws Exception {
        UUID orderId = UUID.randomUUID();

        mockMvc.perform(delete("/orders/" + orderId).with(jwt()))
                .andExpect(status().isOk());

        ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
        verify(orderService).deleteOrder(captor.capture());
        assertThat(captor.getValue()).isEqualTo(orderId);
    }

    @Test
    void deleteOrderNotFoundReturns404() throws Exception {
        UUID orderId = UUID.randomUUID();

        org.mockito.Mockito.doThrow(new ResourceNotFoundException("Order with id " + orderId + " cannot be found"))
                .when(orderService).deleteOrder(orderId);

        mockMvc.perform(delete("/orders/" + orderId).with(jwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void unauthenticatedRequestReturns401() throws Exception {
        mockMvc.perform(patch("/orders/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }
}
