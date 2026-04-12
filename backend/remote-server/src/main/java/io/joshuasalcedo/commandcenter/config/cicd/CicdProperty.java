package io.joshuasalcedo.commandcenter.config.cicd;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author JoshuaSalcedo
 * @since 4/12/2026 11:59 PM
 */
@ConfigurationProperties(prefix = "app.cicd")
public record CicdProperty(
		String localArtifactId,
		String desktopArtifactId
) {
}
