package com.rental.CarRental.service.security;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TokenBlackListService {

    private final Set<String> revokedTokens = new HashSet<>();

    public void revoke(String jti) {
        revokedTokens.add(jti);
    }

    public boolean isRevoked(String jti) {
        return revokedTokens.contains(jti);
    }
}
