package io.joshuasalcedo.commandcenter.user.api.dto;

import io.joshuasalcedo.commandcenter.user.Role;
import io.joshuasalcedo.commandcenter.user.UserId;

import java.time.Instant;

public record UserDTO(
        UserId id,
        String name,
        String email,
        String picture,
        Role role,
        Instant createdAt,
        Instant lastLoginAt
) {
}
