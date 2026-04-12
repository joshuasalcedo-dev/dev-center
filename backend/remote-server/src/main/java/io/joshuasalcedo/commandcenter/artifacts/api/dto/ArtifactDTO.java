package io.joshuasalcedo.commandcenter.artifacts.api.dto;

import java.util.List;

public record ArtifactDTO(
        String id,
        String artifactName,
        List<ReleaseDTO> releases
) {
}
