package io.joshuasalcedo.commandcenter.artifacts.api.dto;

import java.net.URI;

public record ArtifactBinaryDTO(
        String platform,
        URI downloadUrl,
        String hashAlgorithm,
        String hashValue,
        long sizeBytes,
        String signature
) {
}
