package br.com.jobinder.authservice.integration;

import br.com.jobinder.authservice.client.IdentityServiceClient;
import br.com.jobinder.authservice.dto.InternalUserAuthDTO;
import br.com.jobinder.authservice.dto.LoginRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private IdentityServiceClient identityServiceClient;

    @Test
    @DisplayName("Should login successfully and return JWT token")
    void shouldLoginSuccessfully() throws Exception {
        // Given
        String phone = "11999999999";
        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        InternalUserAuthDTO mockUserFromIdentity = new InternalUserAuthDTO(
                UUID.randomUUID(),
                phone,
                encodedPassword,
                "USER"
        );

        LoginRequestDTO loginRequest = new LoginRequestDTO(phone, rawPassword);

        // Mocking
        when(identityServiceClient.getUserAuthDetails(phone))
                .thenReturn(mockUserFromIdentity);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.type").value("Bearer"));
    }

    @Test
    @DisplayName("Should return 401/403 or Error when password is invalid")
    void shouldFailLoginWithInvalidPassword() throws Exception {
        // Given
        String phone = "11999999999";
        String correctPasswordEncoded = passwordEncoder.encode("correctPassword");

        InternalUserAuthDTO mockUserFromIdentity = new InternalUserAuthDTO(
                UUID.randomUUID(),
                phone,
                correctPasswordEncoded,
                "USER"
        );

        LoginRequestDTO loginRequest = new LoginRequestDTO(phone, "wrongPassword");

        // Mocking
        when(identityServiceClient.getUserAuthDetails(phone))
                .thenReturn(mockUserFromIdentity);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Should return 404 Not Found when user does not exist")
    void shouldFailWhenUserNotFound() throws Exception {
        // Given
        LoginRequestDTO loginRequest = new LoginRequestDTO("00000000000", "anyPass");

        // Mocking
        when(identityServiceClient.getUserAuthDetails(anyString()))
                .thenThrow(new RuntimeException("User not found"));

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User Not Found"));
    }
}