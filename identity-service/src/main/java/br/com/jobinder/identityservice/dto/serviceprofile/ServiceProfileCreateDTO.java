package br.com.jobinder.identityservice.dto.serviceprofile;

import jakarta.validation.constraints.NotBlank;

public record ServiceProfileCreateDTO(
        @NotBlank(message = "Specialty name cannot be blank")
        String specialtyName,
        @NotBlank(message = "User ID cannot be blank")
        String city,
        @NotBlank(message = "State cannot be blank")
        String state,
        @NotBlank(message = "Country cannot be blank")
        String serviceImageUrl,
        @NotBlank(message = "Availability cannot be blank")
        String availability,
        @NotBlank(message = "Description cannot be blank")
        String description
) {}
