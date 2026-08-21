package com.rental.CarRental.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<String> handleResourceExistsException(ResourceAlreadyExistsException exception) {
        return ResponseEntity.status(400).body(exception.getMessage());
    }

    @ExceptionHandler(CarAlreadyInUseException.class)
    public ResponseEntity<String> handleCarAlreadyInUseException(CarAlreadyInUseException exception) {
        return ResponseEntity.status(400).body(exception.getMessage());
    }

    @ExceptionHandler(OrderStatusException.class)
    public ResponseEntity<String> handleOrderStatusException(OrderStatusException exception) {
        return ResponseEntity.status(400).body(exception.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException() {
        return ResponseEntity.status(401).body("Incorrect credentials");
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidToken(InvalidTokenException exception) {
        return ResponseEntity.status(401).body(exception.getMessage());
    }

    @ExceptionHandler(ForbiddenAccessException.class)
    public ResponseEntity<String> handleForbiddenAccess(ForbiddenAccessException exception) {
        return ResponseEntity.status(403).body(exception.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException exception) {
        return ResponseEntity.status(404).body(exception.getMessage());
    }

}
