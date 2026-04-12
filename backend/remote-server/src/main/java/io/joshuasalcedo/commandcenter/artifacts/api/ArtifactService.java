package io.joshuasalcedo.commandcenter.artifacts.api;

import io.joshuasalcedo.commandcenter.artifacts.api.dto.ArtifactDTO;
import io.joshuasalcedo.commandcenter.artifacts.api.dto.PublishReleaseRequest;
import io.joshuasalcedo.commandcenter.artifacts.api.dto.ReleaseDTO;

import java.util.List;
import java.util.Optional;

public interface ArtifactService {

    ArtifactDTO create(String artifactName);

    ArtifactDTO findById(String id);

    Optional<ArtifactDTO> findByName(String artifactName);

    List<ArtifactDTO> findAll();

    ReleaseDTO publishRelease(String artifactId, PublishReleaseRequest request);

    Optional<ReleaseDTO> latestRelease(String artifactId);

    Optional<ReleaseDTO> findRelease(String artifactId, String version);

    void delete(String id);
}
