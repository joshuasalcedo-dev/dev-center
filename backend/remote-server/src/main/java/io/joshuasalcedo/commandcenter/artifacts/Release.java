package io.joshuasalcedo.commandcenter.artifacts;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(
    name = "artifact_release",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_release_artifact_version",
        columnNames = {"artifact_id", "version_major", "version_minor", "version_patch"}
    )
)
class Release implements Comparable<Release> {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "release_id"))
    private ReleaseId id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "artifact_id", nullable = false))
    private ArtifactId artifactId;

    @Embedded
    private ArtifactVersion artifactVersion;

    @Column(name = "published_at", nullable = false)
    private Instant publishedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "release_binary",
        joinColumns = @JoinColumn(name = "release_id")
    )
    @MapKeyColumn(name = "platform")
    @MapKeyEnumerated(EnumType.STRING)
    private Map<Platform, ArtifactBinary> binaries = new EnumMap<>(Platform.class);

    protected Release() { /* JPA */ }

    Release(ReleaseId id, ArtifactId artifactId, ArtifactVersion artifactVersion,
            Map<Platform, ArtifactBinary> binaries, Instant publishedAt) {
        if (id == null)          throw new IllegalArgumentException("id required");
        if (artifactId == null)  throw new IllegalArgumentException("artifactId required");
        if (artifactVersion == null)     throw new IllegalArgumentException("artifactVersion required");
        if (publishedAt == null) throw new IllegalArgumentException("publishedAt required");
        if (binaries == null || binaries.isEmpty()) {
            throw new IllegalArgumentException("at least one binary required");
        }
        this.id = id;
        this.artifactId = artifactId;
        this.artifactVersion = artifactVersion;
        this.publishedAt = publishedAt;
        this.binaries = new EnumMap<>(Platform.class);
        binaries.forEach((platform, binary) -> {
            if (binary.platform() != platform) {
                throw new IllegalArgumentException(
                    "binary platform " + binary.platform() + " ≠ map key " + platform);
            }
            this.binaries.put(platform, binary);
        });
    }

    public Optional<ArtifactBinary> binaryFor(Platform platform) {
        return Optional.ofNullable(binaries.get(platform));
    }

    public ReleaseId id()                            { return id; }
    public ArtifactId artifactId()                   { return artifactId; }
    public ArtifactVersion version()                         { return artifactVersion; }
    public Instant publishedAt()                     { return publishedAt; }
    public Map<Platform, ArtifactBinary> binaries()  { return Map.copyOf(binaries); }

    @Override
    public int compareTo(Release other) {
        return this.artifactVersion.compareTo(other.artifactVersion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Release other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}