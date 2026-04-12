package io.joshuasalcedo.commandcenter.apikey;

import io.joshuasalcedo.commandcenter.user.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface ApiKeyRepository extends JpaRepository<ApiKey, String> {

    Optional<ApiKey> findByName(String name);

    List<ApiKey> findAllByOwnerId(UserId ownerId);

    List<ApiKey> findAllByOwnerIdAndRevokedFalse(UserId ownerId);

    List<ApiKey> findAllByRevokedFalse();

    boolean existsByOwnerIdAndName(UserId ownerId, String name);
}
