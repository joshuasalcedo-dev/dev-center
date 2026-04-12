package io.joshuasalcedo.commandcenter.apikey.api.dto;

import io.joshuasalcedo.commandcenter.user.UserId;

import java.time.Instant;

public record ApiKeyDTO(
        String id,
        UserId ownerId,
        String name,
        Instant createdAt,
        Instant expiresAt,
        Instant lastUsedAt,
        boolean revoked,
        boolean valid
) {
}
