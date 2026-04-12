package io.joshuasalcedo.commandcenter.apikey;

import io.joshuasalcedo.commandcenter.ResourceNotFoundException;
import io.joshuasalcedo.commandcenter.UnauthorizedException;
import io.joshuasalcedo.commandcenter.apikey.api.ApiKeyService;
import io.joshuasalcedo.commandcenter.apikey.api.dto.ApiKeyDTO;
import io.joshuasalcedo.commandcenter.user.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
class DefaultApiKeyService implements ApiKeyService {

    private final ApiKeyRepository repository;

    DefaultApiKeyService(ApiKeyRepository repository) {
        this.repository = repository;
    }

    @Override
    public ApiKeyDTO create(UserId owner, String name) {
        return create(owner, name, null);
    }

    @Override
    public ApiKeyDTO create(UserId owner, String name, Instant expiresAt) {
        if (repository.existsByOwnerIdAndName(owner, name)) {
            throw new IllegalStateException("api key with name '" + name + "' already exists for this user");
        }
        ApiKey saved = repository.save(new ApiKey(owner, name, expiresAt));
        return ApiKeyMapper.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiKeyDTO findById(String id) {
        return ApiKeyMapper.from(loadOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApiKeyDTO> findAllByOwner(UserId owner) {
        return repository.findAllByOwnerId(owner).stream()
                .map(ApiKeyMapper::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApiKeyDTO> findAllActiveByOwner(UserId owner) {
        return repository.findAllByOwnerIdAndRevokedFalse(owner).stream()
                .map(ApiKeyMapper::from)
                .toList();
    }

    @Override
    public ApiKeyDTO authenticate(String id) {
        ApiKey key = repository.findById(id)
                .orElseThrow(() -> new UnauthorizedException("unknown api key"));
        if (!key.isValid()) {
            throw new UnauthorizedException(
                    key.isRevoked() ? "api key revoked" : "api key expired");
        }
        key.markUsed();
        return ApiKeyMapper.from(key);
    }

    @Override
    public void revoke(UserId owner, String id) {
        ApiKey key = loadOrThrow(id);
        assertOwner(key, owner);
        key.revoke();
    }

    @Override
    public void delete(UserId owner, String id) {
        ApiKey key = loadOrThrow(id);
        assertOwner(key, owner);
        repository.delete(key);
    }

    private ApiKey loadOrThrow(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("api key not found: " + id));
    }

    private void assertOwner(ApiKey key, UserId owner) {
        if (!key.ownerId().equals(owner)) {
            throw new ResourceNotFoundException("api key not found: " + key.id());
        }
    }
}
