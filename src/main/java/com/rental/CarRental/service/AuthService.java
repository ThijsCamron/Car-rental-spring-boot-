package com.rental.CarRental.service;

import com.rental.CarRental.dto.LoginRequest;
import com.rental.CarRental.dto.LoginResponse;
import com.rental.CarRental.dto.RefreshRequest;
import com.rental.CarRental.service.security.JwtService;
import com.rental.CarRental.service.security.TokenBlackListService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenBlackListService tokenBlackListService;
    private final PasswordEncoder passwordEncoder;
    private final AdminService adminService;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       TokenBlackListService tokenBlackListService,
                       PasswordEncoder passwordEncoder,
                       AdminService adminService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.tokenBlackListService = tokenBlackListService;
        this.passwordEncoder = passwordEncoder;
        this.adminService = adminService;
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(userDetails, false);
        String refreshToken = jwtService.generateToken(userDetails, true);
        String hashedRefreshToken = passwordEncoder.encode(refreshToken);


        if(userDetails.getAuthorities().iterator().next().
                getAuthority().
                equalsIgnoreCase("ROLE_ADMIN")) {

            adminService.updateRefreshToken(request.email(), hashedRefreshToken);
        } else {
            //TODO platformadmin
        }

        //TODO refresh
        return new LoginResponse(accessToken, refreshToken);
    }

    public void logout(Jwt jwt) {
        tokenBlackListService.revoke(jwt.getId());
    }

    public void refresh(RefreshRequest request) {

    }
}
