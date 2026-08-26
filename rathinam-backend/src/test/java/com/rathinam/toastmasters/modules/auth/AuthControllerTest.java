package com.rathinam.toastmasters.modules.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rathinam.toastmasters.config.security.CustomUserDetails;
import com.rathinam.toastmasters.config.security.JwtProvider;
import com.rathinam.toastmasters.modules.account.entity.AccountEntity;
import com.rathinam.toastmasters.modules.account.entity.AccountRole;
import com.rathinam.toastmasters.modules.auth.controller.AuthController;
import com.rathinam.toastmasters.modules.auth.dto.AuthResponse;
import com.rathinam.toastmasters.modules.auth.dto.LoginRequest;
import com.rathinam.toastmasters.modules.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"management.health.db.enabled=false", "spring.jpa.hibernate.ddl-auto=none"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @Test
    void login_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@rathinam.com");
        loginRequest.setPassword("password123");

        AuthResponse authResponse = new AuthResponse("mock-jwt-token", "admin@rathinam.com", AccountRole.ADMIN);
        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.data.email").value("admin@rathinam.com"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    @Test
    void login_Failure_BadCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@rathinam.com");
        loginRequest.setPassword("wrongpassword");

        when(authService.authenticateUser(any(LoginRequest.class))).thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void getMe_Success() throws Exception {
        AccountEntity account = new AccountEntity();
        account.setEmail("admin@rathinam.com");
        account.setRole(AccountRole.ADMIN);
        account.setEnabled(true);
        CustomUserDetails userDetails = new CustomUserDetails(account);

        mockMvc.perform(get("/api/v1/auth/me")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("admin@rathinam.com"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    @Test
    void getMe_WithoutAuth_Fails() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isForbidden());
    }
}
