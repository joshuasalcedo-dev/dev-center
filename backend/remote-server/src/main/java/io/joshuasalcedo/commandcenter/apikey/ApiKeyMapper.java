package io.joshuasalcedo.commandcenter.apikey;

import io.joshuasalcedo.commandcenter.apikey.api.dto.ApiKeyDTO;

class ApiKeyMapper {

    static ApiKeyDTO from(ApiKey entity) {
        return new ApiKeyDTO(
                entity.id(),
                entity.ownerId(),
                entity.name(),
                entity.createdAt(),
                entity.expiresAt(),
                entity.lastUsedAt(),
                entity.isRevoked(),
                entity.isValid()
        );
    }
}
