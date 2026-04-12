package io.joshuasalcedo.commandcenter.artifacts;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface ArtifactRepository extends JpaRepository<Artifact, ArtifactId> {
    Optional<Artifact> findByArtifactName(String artifactName);
    boolean existsByArtifactName(String artifactName);
}
