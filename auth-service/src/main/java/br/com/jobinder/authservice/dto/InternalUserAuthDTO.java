package br.com.jobinder.authservice.dto;

import java.util.UUID;

public record InternalUserAuthDTO(
        UUID id,
        String phone,
        String hashedPassword,
        String role
) {}
