package io.joshuasalcedo.commandcenter.artifacts;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

record DownloadableArtifact(
        ArtifactVersion artifactVersion,
        Map<Platform, ArtifactBinary> binaries
) implements Comparable<DownloadableArtifact> {

    public DownloadableArtifact {
        if (artifactVersion == null) throw new IllegalArgumentException("artifactVersion required");
        if (binaries == null || binaries.isEmpty()) {
            throw new IllegalArgumentException("at least one binary required");
        }
        // defensive copy + validate platform↔key consistency
        Map<Platform, ArtifactBinary> copy = new EnumMap<>(Platform.class);
        binaries.forEach((platform, binary) -> {
            if (binary.platform() != platform) {
                throw new IllegalArgumentException(
                        "binary platform " + binary.platform() +
                        " does not match map key " + platform);
            }
            copy.put(platform, binary);
        });
        binaries = Map.copyOf(copy);  // immutable snapshot
    }

    public Optional<ArtifactBinary> binaryFor(Platform platform) {
        return Optional.ofNullable(binaries.get(platform));
    }

    @Override
    public int compareTo(DownloadableArtifact other) {
        return artifactVersion.compareTo(other.artifactVersion);
    }

    /** Newest-first comparator, useful for sorting release lists. */
    public static Comparator<DownloadableArtifact> newestFirst() {
        return Comparator.comparing(DownloadableArtifact::artifactVersion).reversed();
    }
}