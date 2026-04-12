package io.joshuasalcedo.commandcenter.artifacts.api.dto;

import java.net.URI;

public record UpdateCheckResponse(
        String version,
        URI downloadUrl,
        String hashAlgorithm,
        String hashValue,
        long sizeBytes
) {
}
