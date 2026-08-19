package com.rental.CarRental.dto;

public record CreateAdminResponse(
        String email,
        String firstName,
        String lastName
) {}