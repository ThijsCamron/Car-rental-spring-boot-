package com.rental.CarRental.dto;

public record LoginRequest(
        String email,
        String password
) {
}
