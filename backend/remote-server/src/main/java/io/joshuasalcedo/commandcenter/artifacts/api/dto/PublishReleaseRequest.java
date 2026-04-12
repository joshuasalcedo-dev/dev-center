package io.joshuasalcedo.commandcenter.artifacts.api.dto;

import java.net.URI;
import java.util.List;

public record PublishReleaseRequest(
        String version,
        List<BinaryEntry> binaries
) {
    public record BinaryEntry(
            String platform,
            URI downloadUrl,
            String hashAlgorithm,
            String hashValue,
            long sizeBytes,
            String signature
    ) {
    }
}
