package br.com.jobinder.identity_service.service;

import br.com.jobinder.identityservice.domain.user.User;
import br.com.jobinder.identityservice.domain.user.UserRepository;
import br.com.jobinder.identityservice.service.AuthenticationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("Should return UserDetails when user is found by phone number")
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        // Given
        String phoneNumber = "11999999999";

        var user = new User();
        user.setId(UUID.randomUUID());
        user.setPhone(phoneNumber);
        user.setPassword("hashedPassword");

        // Mocking
        when(userRepository.findByPhone(phoneNumber))
                .thenReturn(Optional.of(user));

        // When
        UserDetails result = authenticationService.loadUserByUsername(phoneNumber);

        // Then
        assertNotNull(result);
        assertEquals(phoneNumber, result.getUsername()); // Username is the phone number
        assertEquals(user.getPassword(), result.getPassword());

        // Verify
        verify(userRepository, times(1))
                .findByPhone(phoneNumber);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user is not found by phone number")
    void loadUserByUsername_WhenUserNotFound_ShouldThrowException() {
        // Given
        String phoneNumber = "11888888888";

        // Mocking
        when(userRepository.findByPhone(phoneNumber))
                .thenReturn(Optional.empty());

        // When
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            authenticationService.loadUserByUsername(phoneNumber);
        });

        // Then
        assertEquals("User not found with phone:" + phoneNumber, exception.getMessage());

        // Verify
        verify(userRepository, times(1))
                .findByPhone(phoneNumber);
    }
}