package br.com.jobinder.authservice.service;

import br.com.jobinder.authservice.client.IdentityServiceClient;
import br.com.jobinder.authservice.dto.InternalUserAuthDTO;
import br.com.jobinder.authservice.dto.LoginRequestDTO;
import br.com.jobinder.authservice.dto.LoginResponseDTO;
import br.com.jobinder.authservice.infra.exception.UserNotFoundException;
import br.com.jobinder.authservice.infra.security.JwtTokenProvider;
import br.com.jobinder.authservice.infra.exception.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private IdentityServiceClient identityServiceClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        InternalUserAuthDTO userDetails;

        try {
            userDetails = identityServiceClient.getUserAuthDetails(loginRequest.phone());
        } catch (Exception e) {
            throw new UserNotFoundException("User not found with phone: " + loginRequest.phone());
        }

        if (userDetails == null) {
            throw new UserNotFoundException("User not found with phone: " + loginRequest.phone());
        }

        // Validate password
        if (!passwordEncoder.matches(loginRequest.password(), userDetails.hashedPassword())) {
            throw new InvalidCredentialsException("Invalid phone number or password.");
        }

        // Generate JWT token
        String token = tokenProvider.generateToken(userDetails);

        return new LoginResponseDTO(token, "Bearer");
    }
}