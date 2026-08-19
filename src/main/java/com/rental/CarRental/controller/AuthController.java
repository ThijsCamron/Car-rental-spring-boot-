package com.rental.CarRental.controller;

import com.rental.CarRental.dto.LoginRequest;
import com.rental.CarRental.dto.LoginResponse;
import com.rental.CarRental.dto.RefreshRequest;
import com.rental.CarRental.service.AuthService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @DeleteMapping("/logout")
    public void logout(@AuthenticationPrincipal Jwt jwt) {
        authService.logout(jwt);
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@AuthenticationPrincipal Jwt jwt, @RequestBody RefreshRequest request) {
        authService.refresh(request);
    }


}
