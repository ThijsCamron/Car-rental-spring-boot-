package com.rental.CarRental.entity;

public interface JwtUser {
    void setRefreshToken(String refreshToken);
    String getRefreshToken();
}
