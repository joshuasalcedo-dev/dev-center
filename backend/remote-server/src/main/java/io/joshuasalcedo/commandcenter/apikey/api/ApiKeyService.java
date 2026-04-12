package io.joshuasalcedo.commandcenter.apikey.api;

import io.joshuasalcedo.commandcenter.apikey.api.dto.ApiKeyDTO;
import io.joshuasalcedo.commandcenter.user.UserId;

import java.time.Instant;
import java.util.List;

public interface ApiKeyService {

    ApiKeyDTO create(UserId owner, String name);

    ApiKeyDTO create(UserId owner, String name, Instant expiresAt);

    ApiKeyDTO findById(String id);

    List<ApiKeyDTO> findAllByOwner(UserId owner);

    List<ApiKeyDTO> findAllActiveByOwner(UserId owner);

    /** Authenticates an incoming key. Throws if invalid. Updates lastUsedAt. Returns the key with its owner. */
    ApiKeyDTO authenticate(String id);

    void revoke(UserId owner, String id);

    void delete(UserId owner, String id);
}
