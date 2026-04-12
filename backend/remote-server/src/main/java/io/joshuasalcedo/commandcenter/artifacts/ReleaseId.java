package io.joshuasalcedo.commandcenter.artifacts;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

record ReleaseId(UUID value) {

    public ReleaseId {
        if (value == null) throw new IllegalArgumentException("value required");
    }

    public static ReleaseId newId() {
        return new ReleaseId(UUID.randomUUID());
    }

    /** Deterministic ID from artifact + artifactVersion — useful for idempotent ingestion. */
    public static ReleaseId deterministic(ArtifactId artifactId, ArtifactVersion artifactVersion) {
        String seed = artifactId.value() + ":" + artifactVersion;
        return new ReleaseId(UUID.nameUUIDFromBytes(seed.getBytes(StandardCharsets.UTF_8)));
    }
}