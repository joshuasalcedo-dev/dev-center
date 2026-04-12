package io.joshuasalcedo.commandcenter.config.cicd;

/**
 * @author JoshuaSalcedo
 * @since 4/13/2026 12:01 AM
 */

public record Cicd(
		CiCdArtifact localArtifact,
		CiCdArtifact desktopArtifact
) {
}
