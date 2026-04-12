package io.joshuasalcedo.commandcenter.config.cicd;

import io.joshuasalcedo.commandcenter.artifacts.ArtifactId;

/**
 * @author JoshuaSalcedo
 * @since 4/13/2026 12:00 AM
 */

public record CiCdArtifact(
		String name,
		 ArtifactId id
) {
}
