package com.rental.CarRental.controller;

import com.rental.CarRental.dto.LoginRequest;
import com.rental.CarRental.dto.LoginResponse;
import com.rental.CarRental.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    void testLoginPassesCorrectRequestToService() throws Exception {
        LoginRequest loginRequest = new LoginRequest(
                "test@example.com",
                "password"
        );

        LoginResponse loginResponse = new LoginResponse(
                "access-token",
                "refresh-token"
        );

        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
        ).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.accessToken").value("access-token")).
                andExpect(jsonPath("$.refreshToken").value("refresh-token"));

        ArgumentCaptor<LoginRequest> captor = ArgumentCaptor.forClass(LoginRequest.class);
        verify(authService).login(captor.capture());
        assertThat(captor.getValue().username()).isEqualTo("test@example.com");
        assertThat(captor.getValue().password()).isEqualTo("password");
    }

    @Test
    void testLoginWithIncorrectCredentialsFails() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Incorrect credentials"));

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest("invalid", "invalid")
                        )))
                .andExpect(status().isUnauthorized()).
                andExpect(content().string("Invalid credentials"));
    }

    @Test
    void testLogoutSucceedsAndPassesJwtToService() throws Exception {
        mockMvc.perform(delete("/auth/logout")
                .with(jwt().jwt(j -> j.jti("id-1").
                        subject("test@test.com"))))
                .andExpect(status().isOk());

        ArgumentCaptor<Jwt> captor = ArgumentCaptor.forClass(Jwt.class);
        verify(authService).logout(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo("id-1");
    }
}
