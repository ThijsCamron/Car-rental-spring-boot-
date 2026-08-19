package com.rental.CarRental.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @NotBlank(message = "Refresh token cannot be empty") String refreshToken
) {
}
