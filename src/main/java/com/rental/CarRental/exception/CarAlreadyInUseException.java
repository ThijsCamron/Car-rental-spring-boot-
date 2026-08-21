package com.rental.CarRental.exception;

public class CarAlreadyInUseException extends RuntimeException {
    public CarAlreadyInUseException(String message) {
        super(message);
    }
}
