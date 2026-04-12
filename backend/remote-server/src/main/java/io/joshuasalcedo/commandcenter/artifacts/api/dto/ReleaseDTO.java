package io.joshuasalcedo.commandcenter.artifacts.api.dto;

import java.time.Instant;
import java.util.List;

public record ReleaseDTO(
        String releaseId,
        String version,
        Instant publishedAt,
        List<ArtifactBinaryDTO> binaries
) {
}
