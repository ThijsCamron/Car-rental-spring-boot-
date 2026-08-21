package com.rental.CarRental.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.rental.CarRental.dto.LoginRequest;
import com.rental.CarRental.dto.LoginResponse;
import com.rental.CarRental.dto.RefreshRequest;
import com.rental.CarRental.entity.JwtUser;
import com.rental.CarRental.exception.InvalidTokenException;
import com.rental.CarRental.service.security.JwtService;
import com.rental.CarRental.service.security.TokenBlackListService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenBlackListService tokenBlackListService;
    private final PasswordEncoder passwordEncoder;
    private final AdminService adminService;
    private final PlatformAdminService platformAdminService;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       TokenBlackListService tokenBlackListService,
                       PasswordEncoder passwordEncoder,
                       AdminService adminService, PlatformAdminService platformAdminService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.tokenBlackListService = tokenBlackListService;
        this.passwordEncoder = passwordEncoder;
        this.adminService = adminService;
        this.platformAdminService = platformAdminService;
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
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

            adminService.updateRefreshToken(request.username(), hashedRefreshToken);
        } else {
            platformAdminService.updateRefreshToken(UUID.fromString(request.username()), hashedRefreshToken);
        }

        return new LoginResponse(accessToken, refreshToken);
    }

    public void logout(Jwt jwt) {
        Duration remaining = Duration.between(Instant.now(), jwt.getExpiresAt());

        if(!remaining.isNegative() && !remaining.isZero()) {
            tokenBlackListService.revoke(jwt.getId(), remaining);
        }
    }

    public LoginResponse refresh(RefreshRequest request) {
        DecodedJWT jwt = jwtService.validateJwt(request.refreshToken());
        String username = jwt.getSubject();
        String role = String.valueOf(jwt.getClaims().get("role"));
        UserDetails userDetails = User.withUsername(username).authorities(role).build();
        JwtUser user;

        if(role.equalsIgnoreCase("ROLE_ADMIN")) {
            user = adminService.getAdminByEmail(username);
        } else {
            user = platformAdminService.getAdminById(UUID.fromString(username));
        }

        if(!passwordEncoder.matches(request.refreshToken(), user.getRefreshToken())) {
            throw new InvalidTokenException("Refresh token " + request.refreshToken() + " is invalid");
        }

        String accessToken = jwtService.generateToken(userDetails, false);
        String refreshToken = jwtService.generateToken(userDetails, true);
        String hashedRefreshToken = passwordEncoder.encode(refreshToken);

        if(role.equalsIgnoreCase("ROLE_ADMIN")) {
            adminService.updateRefreshToken(username, hashedRefreshToken);
        } else {
            platformAdminService.updateRefreshToken(UUID.fromString(username), hashedRefreshToken);
        }

        return new LoginResponse(accessToken, refreshToken);
    }
}
