package io.joshuasalcedo.commandcenter.artifacts;

import java.util.Comparator;

record ArtifactVersion(int major, int minor, int patch) implements Comparable<ArtifactVersion> {

    private static final Comparator<ArtifactVersion> COMPARATOR =
            Comparator.comparingInt(ArtifactVersion::major)
                      .thenComparingInt(ArtifactVersion::minor)
                      .thenComparingInt(ArtifactVersion::patch);

    public ArtifactVersion {
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException("artifactVersion components must be non-negative");
        }
    }

    public static ArtifactVersion of(int major, int minor, int patch) {
        return new ArtifactVersion(major, minor, patch);
    }

    /** Parses "1.2.3". Leading "v" tolerated ("v1.2.3"). */
    public static ArtifactVersion parse(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("artifactVersion text must not be blank");
        }
        String trimmed = text.startsWith("v") || text.startsWith("V")
                ? text.substring(1)
                : text;
        String[] parts = trimmed.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("expected major.minor.patch, got: " + text);
        }
        try {
            return new ArtifactVersion(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2])
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("invalid numeric component in: " + text, e);
        }
    }

    @Override
    public int compareTo(ArtifactVersion other) {
        return COMPARATOR.compare(this, other);
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}