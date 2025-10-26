package br.com.jobinder.identityservice.dto.user;

import br.com.jobinder.identityservice.domain.user.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserCreateDTO(
        @NotBlank(message = "Country code cannot be blank")
        String countryCode,
        @NotBlank(message = "National number cannot be blank")
        String nationalNumber,
        @NotBlank(message = "First name cannot be blank")
        String firstName,
        @NotBlank(message = "Last name cannot be blank")
        String lastName,
        @NotBlank(message = "Password cannot be blank")
        String password,
        @NotNull(message = "Role cannot be null")
        UserRole role
) {}
