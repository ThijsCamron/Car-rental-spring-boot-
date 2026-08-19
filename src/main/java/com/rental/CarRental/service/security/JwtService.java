package com.rental.CarRental.service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    @Value("${jwt.access-token.expiration}")
    private int accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private int refreshTokenExpiration;

    private final Algorithm algorithm;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.algorithm = Algorithm.HMAC256(secret);
    }

    public String generateToken(UserDetails userDetails, boolean isRefresh) {

        int duration = isRefresh ? refreshTokenExpiration : accessTokenExpiration;
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withClaim("role", role)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + duration * 1000L))
                .withJWTId(UUID.randomUUID().toString())
                .sign(algorithm);
    }
}
