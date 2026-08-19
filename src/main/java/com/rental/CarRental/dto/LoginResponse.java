package com.rental.CarRental.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}
