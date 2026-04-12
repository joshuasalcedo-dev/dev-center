package io.joshuasalcedo.commandcenter.artifacts;

import jakarta.persistence.*;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "artifact")
class Artifact extends AbstractAggregateRoot<Artifact> {

	@EmbeddedId
	@AttributeOverride(name = "value", column = @Column(name = "artifact_id"))
	private ArtifactId id;

	@Column(name = "artifact_name", nullable = false, unique = true, updatable = false)
	private String artifactName;

	@OneToMany(
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			fetch = FetchType.LAZY
	)
	@JoinColumn(name = "artifact_id", referencedColumnName = "artifact_id")
	@OrderBy("artifactVersion.major ASC, artifactVersion.minor ASC, artifactVersion.patch ASC")
	private Set<Release> releases = new TreeSet<>();

	protected Artifact() { /* JPA */ }

	public Artifact(ArtifactId id, String artifactName) {
		if (id == null) throw new IllegalArgumentException("id required");
		if (artifactName == null || artifactName.isBlank()) {
			throw new IllegalArgumentException("artifactName required");
		}
		this.id = id;
		this.artifactName = artifactName;
	}

	// --- Domain behavior ---

	public Release publishRelease(ArtifactVersion artifactVersion,
	                              Map<Platform, ArtifactBinary> binaries) {
		if (releaseOf(artifactVersion).isPresent()) {
			throw new IllegalStateException(
					"artifactVersion " + artifactVersion + " already released for " + artifactName);
		}
		Release release = new Release(
				ReleaseId.deterministic(id, artifactVersion),
				id,
				artifactVersion,
				binaries,
				Instant.now()
		);
		releases.add(release);
		registerEvent(new ArtifactReleasePublishedEvent(id, release.id(), artifactVersion));
		return release;
	}

	public Optional<Release> latestRelease() {
		return releases.stream().max(Comparator.naturalOrder());
	}

	public Optional<Release> releaseOf(ArtifactVersion artifactVersion) {
		return releases.stream()
				.filter(r -> r.version().equals(artifactVersion))
				.findFirst();
	}

	// --- Accessors ---

	public ArtifactId id()           { return id; }
	public String artifactName()     { return artifactName; }
	public Set<Release> releases()   { return Collections.unmodifiableSet(releases); }
}