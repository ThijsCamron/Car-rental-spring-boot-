package com.rental.CarRental.service.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST, "/rental-companies").hasRole("PLATFORM_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/rental-companies/*").hasRole("PLATFORM_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/rental-companies/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/rental-companies/*/admins").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(Customizer.withDefaults())
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            CustomUserDetailsService customUserDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Component
    public class RevokedTokenValidator implements OAuth2TokenValidator<Jwt> {

        private final TokenBlackListService tokenBlackListService;

        public RevokedTokenValidator(TokenBlackListService tokenBlackListService) {
            this.tokenBlackListService = tokenBlackListService;
        }

        @Override
        public OAuth2TokenValidatorResult validate(Jwt token) {
            if(tokenBlackListService.isRevoked(token.getId())) {
                OAuth2Error error = new OAuth2Error("invalid_token", "Token has been revoked", null);
                return OAuth2TokenValidatorResult.failure(error);
            }

            return OAuth2TokenValidatorResult.success();
        }
    }

    @Bean
    public JwtDecoder jwtDecoder(@Value("${jwt.secret}") String secret,
                                 RevokedTokenValidator revokedTokenValidator) {
        SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        decoder.setJwtValidator(revokedTokenValidator);

        return decoder;
    }
}
