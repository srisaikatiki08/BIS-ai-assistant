package com.bis.assistant.controller;

import com.bis.assistant.dto.AuthResponse;
import com.bis.assistant.dto.LoginRequest;
import com.bis.assistant.dto.SignupRequest;
import com.bis.assistant.dto.UserProfileDTO;
import com.bis.assistant.security.CustomUserDetailsService;
import com.bis.assistant.security.JwtAuthenticationEntryPoint;
import com.bis.assistant.security.JwtTokenProvider;
import com.bis.assistant.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void signup_ValidRequest_ReturnsCreated() throws Exception {
        SignupRequest request = new SignupRequest("Rajesh Kumar", "rajesh@example.com", "Password@123");
        UserProfileDTO profile = new UserProfileDTO(1L, "Rajesh Kumar", "rajesh@example.com", LocalDateTime.now());
        AuthResponse response = AuthResponse.success("jwt.token.here", profile, "User registered successfully.");

        when(authService.signup(any(SignupRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value("jwt.token.here"))
                .andExpect(jsonPath("$.user.email").value("rajesh@example.com"));
    }

    @Test
    void signup_InvalidEmail_ReturnsBadRequest() throws Exception {
        SignupRequest request = new SignupRequest("Rajesh Kumar", "invalid-email", "Password@123");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ValidCredentials_ReturnsOk() throws Exception {
        LoginRequest request = new LoginRequest("rajesh@example.com", "Password@123");
        UserProfileDTO profile = new UserProfileDTO(1L, "Rajesh Kumar", "rajesh@example.com", LocalDateTime.now());
        AuthResponse response = AuthResponse.success("jwt.token.here", profile, "Login successful.");

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value("jwt.token.here"))
                .andExpect(jsonPath("$.user.email").value("rajesh@example.com"));
    }

    @Test
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest("rajesh@example.com", "WrongPassword");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid email or password."));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Invalid email or password."));
    }

    @Test
    @WithMockUser(username = "rajesh@example.com", roles = {"USER"})
    void me_AuthenticatedUser_ReturnsProfile() throws Exception {
        UserProfileDTO profile = new UserProfileDTO(1L, "Rajesh Kumar", "rajesh@example.com", LocalDateTime.now());
        when(authService.getCurrentUserProfile("rajesh@example.com")).thenReturn(profile);

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("rajesh@example.com"))
                .andExpect(jsonPath("$.fullName").value("Rajesh Kumar"));
    }

    @Test
    void me_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }
}
